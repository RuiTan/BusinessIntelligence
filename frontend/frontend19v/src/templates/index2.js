window.addEventListener('load', initAll);

function initAll() {
    $(document).ready(function () {
        $('#fountainG').hide();
        //上传文件按钮
        $('#submit_btn').click(function (e) {
            cleanShow();
            var formData = new FormData();
            formData.append('file', $('#file')[0].files[0]);
            formData.append("algorithm", $('#select')[0].value);
            formData.append('format', $('#format')[0].value);
            $.ajax({
                // url: 'http://10.60.38.182:10080/search/' + $('#select')[0].value,
                url: 'http://10.60.38.173:10080/causality',

                // url: window.location + "causality",
                // url: window.location + "search/"+$('#select')[0].value,
                type: "POST",
                processData: false,
                contentType: false,
                data: formData,
                dataType: 'json',
                crossDomain: true,
                success: function (fb_data) {
                    console.log(JSON.stringify(fb_data))
                    // $('#fountainG').show();
                    // console.log(fb_data);
                    // if (fb_data['msg'] == 'Success' && fb_data['code'] == 200) {
                    // console.log(fb_data);
                    if (fb_data.code == 500)
                    {
                        cleanShow();
                        var error_message = fb_data.message;
                        alert(error_message);
                        return;
                    }
                    if (fb_data.message == 'Success' && fb_data.code == 200) {
                        // console.log(fb_data['data']);
                        let analysis_result = $.parseJSON(fb_data.data);
                        cleanShow();
                        buildShow(analysis_result);
                    }

                    function buildShow(jsonObj) {
                        var text_tree_show_div = d3.select('body').append('div').attr('class', 'text_tree_div');
                        var text_div = text_tree_show_div.append('div').attr('class', 'text_div');
                        var p1 = text_div.append('p').attr('class', 'p1').text('当前选中节点：');
                        var span1 = p1.append('span').text('无');
                        var paths_string_div = text_div.append('div').attr('class', 'paths_string_div');
                        var data = jsonObj['edgeLists'];
                        var nodesName = [];
                        var my_nodes = []; //{name:node_name}

                        var my_links = [];  //有向边{source,target}
                        var my_lines = [];  //无向边{source, target}

                        var clear_cause_type = 1; // -->
                        var possible_cause_type_line = 3; // --, o--o
                        var possible_cause_type_o_a = 4; // o-->
                        for (var item in data) {
                            let edgeSets = data[item]; //获得边集
                            for (let i = 0; i < edgeSets.length; i++) //依次处理每个边集中的边
                            {
                                var edge = edgeSets[i];
                                var node1;
                                var node2;
                                if (edge.hasOwnProperty('node1') && edge.hasOwnProperty('node2')) {
                                    node1 = edge['node1'];
                                    node2 = edge['node2'];
                                    let node1Type = edge['endpoint1'].ordinal;
                                    let node2Type = edge['endpoint2'].ordinal;
                                    let node1name = node1.name;
                                    let node2name = node2.name;
                                    let link;
                                    let node1Index = -1;
                                    let node2Index = -1;
                                    //处理点的信息
                                    if (nodesName.indexOf(node1name) === -1) {
                                        nodesName[nodesName.length] = node1name;
                                        my_nodes[my_nodes.length] = {name: node1name};
                                    }
                                    if (nodesName.indexOf(node2name) === -1) {
                                        nodesName[nodesName.length] = node2name;
                                        my_nodes[my_nodes.length] = {name: node2name};
                                    }
                                    node1Index = nodesName.indexOf(node1name);
                                    node2Index = nodesName.indexOf(node2name);
                                    //把该边信息加入my_links/my_lines
                                    link = {source: node1Index, target: node2Index};
                                    if (node1Type == 1 && node2Type == 0) { // <--
                                        if (!is_included_in_links(my_links, node2Index, node1Index)) {
                                            my_links.push({
                                                source: node2Index,
                                                target: node1Index,
                                                link_type: clear_cause_type
                                                // is_biorientation: false
                                            });
                                        }
                                    }
                                    else if (node1Type == 0 && node2Type == 1) { // -->
                                        if (!is_included_in_links(my_links, node1Index, node2Index)) {
                                            my_links.push({
                                                source: node1Index,
                                                target: node2Index,
                                                link_type: clear_cause_type
                                                // is_biorientation: false
                                            });
                                        }
                                    }
                                    else if (node1Type == 1 && node2Type == 2) { // <--o
                                        if (!is_included_in_links(my_links, node2Index, node1Index)) {
                                            my_links.push({
                                                source: node2Index,
                                                target: node1Index,
                                                link_type: possible_cause_type_o_a
                                                // is_biorientation: false
                                            });
                                        }
                                    }
                                    else if (node1Type == 2 && node2Type == 1) // o-->
                                    {
                                        if (!is_included_in_links(my_links, node1Index, node2Index)) {
                                            my_links.push({
                                                source: node1Index,
                                                target: node2Index,
                                                link_type: possible_cause_type_o_a
                                                // is_biorientation: false
                                            });
                                        }
                                    }
                                    else if ((node1Type == 2 && node2Type == 2) || (node1Type == 0 && node2Type == 0)) // o--o 两边都是圈的情况, -- 一条线的情况
                                    {
                                        if (!is_included_in_links(my_lines, node1Index, node2Index)) {
                                            my_lines.push({
                                                source: node1Index,
                                                target: node2Index,
                                                // link_type: possible_cause_type_o_o
                                                link_type: possible_cause_type_line
                                                // is_biorientation: true
                                            });
                                        }
                                    }
                                }
                            }

                        }

                        function is_included_in_links(_links, _source, _target) {
                            for (var i = 0; i < _links.length; i++) {
                                let cur_link = _links[i];
                                if (cur_link.source === _source && cur_link.target === _target) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        build(my_nodes, my_links, my_lines);

                        function build(nodes, links, lines) {

                            var width = 1024;
                            var height = 1024;
                            var total_links_lines = [];
                            var total_links_lines_2 = [];
                            var node_distance = 180; // 节点之间的距离
                            var sources = [];  // 记录点击的节点的因节点,存的是节点的index
                            var sources_layer = []; //记录每个因节点对应的因的层级
                            var sources_path = []; //记录因节点path箭头
                            var sources_line = []; //记录可能因节点的线条
                            var circle_r = 20; // 节点的半径
                            var current_clicked_node_name = ''; // 记录当前点击的节点的名字
                            // var is_clicked_twice = false; // 判断是否点击相同的点，是的话变回原色

                            //节点颜色
                            var usual_color = "#8B658B";
                            var target_color = "#00CED1";
                            // var cause_color = "red";
                            //构建了11层的因节点颜色
                            var cause_layer_color =
                                [
                                    '#f188df',
                                    '#FFC0CB',
                                    '#f19c88',
                                    '#f19188',
                                    '#f18889',
                                    '#f1889f',
                                    '#f188aa',
                                    '#f188c3',
                                    '#f188d0'
                                    // '#f188df'
                                ];

                            //path颜色
                            var usual_path_color = '#62aeef';

                            //line颜色，无向线
                            var usual_line_color = '#add8e6';

                            var clear_path_color = '#f00';
                            var possible_path_color = '#f00'; //#67f4ea
                            var possible_line_color = '#f00'; //#67f4ea

                            //用于text区域展示
                            var target_source_json_obj = {};
                            var paths_string = []; //用于展示根源路径的字符串

                            for (var i = 0; i < links.length; i++) {
                                total_links_lines.push(links[i]);
                                // total_links_lines_2.push(JSON.parse(JSON.stringify(links[i])));
                            }
                            for (var i = 0; i < lines.length; i++) {
                                total_links_lines.push(lines[i]);
                                // total_links_lines_2.push(JSON.parse(JSON.stringify(lines[i])));
                            }

                            function buildTotalLinksLines2()
                            {
                                total_links_lines_2 = [];
                                for(var i=0;i<total_links_lines.length;i++)
                                {
                                    total_links_lines_2.push(JSON.parse(JSON.stringify(total_links_lines[i])));
                                }
                            }
                            if (window.innerWidth) {
                                width = window.innerWidth;
                            }
                            else if (document.body && document.body.clientWidth) {
                                width = document.body.clientWidth;
                            }
                            if (window.innerHeight) {
                                height = window.innerHeight;
                            }
                            else if (document.body && document.body.clientHeight) {
                                height = document.body.clientHeight;
                            }

                            var svg = d3.select("body")
                                .append("svg")
                                .attr("width", width)
                                .attr("height", height)
                                .attr('class', 'total_graph');
                            // 通过布局来转换数据，然后进行绘制
                            var simulation = d3.forceSimulation(nodes)
                            // .force("link", d3.forceLink(links).distance(100))
                                .force("link", d3.forceLink(total_links_lines).distance(node_distance))
                                .force("charge", d3.forceManyBody())//创建多体力
                                .force("center", d3.forceCenter(width * 0.6, height / 2));

                            simulation
                                .nodes(nodes)//设置力模拟的节点
                                .on("tick", ticked);

                            simulation.force("link")//添加或移除力
                            // .links(links);//设置连接数组
                                .links(total_links_lines);//设置连接数组
                            var color = d3.scaleOrdinal(d3.schemeCategory20);
                            //添加描述节点的文字
                            var svg_texts = svg.selectAll("text")
                                .data(nodes)
                                .enter()
                                .append("text")
                                .style("fill", "black")
                                .attr("dx", 20)
                                .attr("dy", 8)
                                .text(function (d) {
                                    return d.name;
                                });
                            var svg_links = svg.selectAll("path")
                                .data(links)
                                .enter()
                                .append("path")
                                .style("stroke", usual_path_color)
                                .style("stroke-width", 3)
                                // .attr("marker-mid", "url(#arrow)")
                                .call(d3.zoom()//创建缩放行为
                                    .scaleExtent([-5, 2])//设置缩放范围
                                );
                            //绘制不带箭头的线line
                            var svg_lines = svg.selectAll('line')
                                .data(lines)
                                .enter()
                                .append('line')
                                .style('stroke', function (d, i) {
                                    return usual_line_color;
                                })
                                .style('stroke-width', function (d, i) {
                                    return 2;
                                })
                                .call(d3.zoom().scaleExtent([-5, 2]));

                            //绘制节点
                            var svg_nodes = svg.selectAll("circle")
                                .data(nodes)
                                .enter()
                                .append("circle")
                                .attr("cx", function (d) {
                                    return d.x;
                                })
                                .attr("cy", function (d) {
                                    return d.y;
                                })
                                .attr("r", circle_r)
                                .attr("fill", function (d, i) {
                                    // return color(i);
                                    return usual_color;
                                }).call(d3.drag().on("start", dragstarted)//d3.drag() 创建一个拖曳行为
                                    .on("drag", dragged)
                                    .on("end", dragended))
                                .on('click', function (d, i) {
                                    if (current_clicked_node_name == d.name)  //点击了两次
                                    {
                                        current_clicked_node_name = '';
                                        Recover();
                                    }
                                    else {
                                        current_clicked_node_name = d.name;
                                        Show(d.name);
                                    }
                                });

                            //绘制箭头在线条中间的path
                            function drawLineArrow(x1, y1, x2, y2) {
                                var path;
                                var slopy, cosy, siny;
                                var Par = 10.0;
                                var x3, y3;
                                slopy = Math.atan2((y1 - y2), (x1 - x2));
                                cosy = Math.cos(slopy);
                                siny = Math.sin(slopy);
                                path = "M" + x1 + "," + y1 + " L" + x2 + "," + y2;
                                x3 = (Number(x1) + Number(x2)) / 2;
                                y3 = (Number(y1) + Number(y2)) / 2;
                                path += " M" + x3 + "," + y3;
                                path += " L" + (Number(x3) + Number(Par * cosy - (Par / 2.0 * siny))) + "," + (Number(y3) + Number(Par * siny + (Par / 2.0 * cosy)));
                                path += " M" + (Number(x3) + Number(Par * cosy + Par / 2.0 * siny) + "," + (Number(y3) - Number(Par / 2.0 * cosy - Par * siny)));
                                path += " L" + x3 + "," + y3;
                                return path;
                            }

                            function dragstarted(d) {
                                if (!d3.event.active) simulation.alphaTarget(0.3).restart();//设置目标α
                                d.fx = d.x;
                                d.fy = d.y;
                            }

                            function dragged(d) {
                                d.fx = d3.event.x;
                                d.fy = d3.event.y;
                            }

                            function dragended(d) {
                                if (!d3.event.active) simulation.alphaTarget(0);
                                d.fx = null;
                                d.fy = null;
                            }

                            function ticked() {
                                svg_links.attr('d', function (d) {
                                    return drawLineArrow(d.source.x, d.source.y, d.target.x, d.target.y);
                                });

                                svg_lines.attr("x1", function (d) {
                                    return d.source.x;
                                })
                                    .attr("y1", function (d) {
                                        return d.source.y;
                                    })
                                    .attr("x2", function (d) {
                                        return d.target.x;
                                    })
                                    .attr("y2", function (d) {
                                        return d.target.y;
                                    });
                                // .attr('marker-end','url(#arrow)');

                                svg_nodes.attr("cx", function (d) {
                                    return d.x;
                                })
                                    .attr("cy", function (d) {
                                        return d.y;
                                    });

                                svg_texts.attr("x", function (d) {
                                    return d.x;
                                })
                                    .attr("y", function (d) {
                                        return d.y;
                                    });
                            }

                            function Show(_nodeName) {
                                searchSources(_nodeName);
                                hilightNodes();
                                hilightPathsAndLines();
                                update_text();
                            }

                            function Recover() {
                                recoverNodes();
                                recoverPathsAndLines();
                                recoverText();
                            }

                            //******** target_source_json_obj格式：*******//
                            // {
                            //      name:
                            //      children:[
                            //          {
                            //              name:
                            //              index:
                            //              type:
                            //              children:[......]
                            //          }
                            //          {......}
                            //              ]
                            //  }
                            //*******************************************//
                            function searchSources(_nodeName) {
                                buildTotalLinksLines2();
                                sources = [];
                                sources_layer = [];
                                sources_path = [];
                                sources_line = [];
                                var nodeIndex = nodesName.indexOf(_nodeName);
                                if (nodeIndex === -1) {
                                    return;
                                }
                                sources.push(nodeIndex);
                                sources_layer.push(0); //本身节点是第0层
                                //target的json数据
                                var target_json = {};
                                target_json.children = [];
                                target_json.name = _nodeName;
                                target_json.index = nodeIndex;
                                target_json.type = 0; //0代表这是target节点
                                target_source_json_obj = target_json;
                                //利用广度优先搜索算法来向上搜索source节点
                                _searchSources(nodeIndex, 1, target_json); // 1代表搜索第一层因节点
                                // recoverBiorientation();
                            }

                            function _searchSources(_target_index, current_layer, _target_json) {
                                var sub_sources = [];
                                var target_index = _target_index;
                                var splice_index = [];

                                //***********在所有links和line中查找
                                // for (let link_index = 0; link_index < total_links_lines_2.length; link_index++) {
                                for (let link_index = 0; link_index <total_links_lines_2.length; link_index++) {
                                    // console.log(total_links_lines_2.length);
                                    let link = total_links_lines_2[link_index];
                                    if (link.link_type === possible_cause_type_line)
                                    {
                                        // if (link.is_biorientation)
                                        // {
                                        if (link.source.index === target_index)
                                        {
                                            if (sub_sources.includes(link.target.index)) {
                                                splice_index.push(link_index);
                                                continue;
                                            }
                                            sub_sources.push(link.target.index);
                                            var _source_json = {};
                                            _source_json.children = [];
                                            _source_json.name = link.target.name;
                                            _source_json.index = link.target.index;
                                            _source_json.type = link.link_type;
                                            _target_json.children.push(_source_json);
                                            sources_line.push(JSON.parse(JSON.stringify(link)));
                                            sources_layer.push(current_layer);
                                            splice_index.push(link_index);
                                        } else if (link.target.index === target_index) {
                                            if (sub_sources.includes(link.source.index)) {
                                                splice_index.push(link_index);
                                                continue;
                                            }
                                            var _source_json = {};
                                            _source_json.children = [];
                                            _source_json.name = link.source.name;
                                            _source_json.index = link.source.index;
                                            _source_json.type = link.link_type;
                                            _target_json.children.push(_source_json);
                                            sub_sources.push(_source_json.index);
                                            link.is_biorientation = false;
                                            sources_line.push(JSON.parse(JSON.stringify(link)));
                                            sources_layer.push(current_layer);
                                            splice_index.push(link_index);

                                        }

                                        // }
                                    }
                                    else if (link.target.index === target_index) {
                                        if (sub_sources.includes(link.source.index)) {
                                            splice_index.push(link_index);
                                            continue;
                                        }
                                        var _source_json = {};
                                        _source_json.children = [];
                                        _source_json.name = link.source.name;
                                        _source_json.index = link.source.index;
                                        _source_json.type = link.link_type;
                                        _target_json.children.push(_source_json);
                                        sub_sources.push(link.source.index);
                                        sources_layer.push(current_layer);
                                        sources_path.push(JSON.parse(JSON.stringify(link)));
                                        splice_index.push(link_index);
                                    }
                                }
                                // console.log(splice_index);
                                if (sub_sources.length < 1) {
                                    return;
                                }
                                total_links_lines_2 = total_links_lines_2.filter((item,index) =>{
                                    return splice_index.indexOf(index) == -1;
                                } );
                                // console.log(total_links_lines_2.length);
                                // var temp_sources =
                                sources.push.apply(sources, sub_sources);
                                // for(var i=0;i<sub_sources.length;i++)
                                // {
                                //     sources.push(sub_sources[i]);
                                // }
                                for (let i = 0; i < sub_sources.length; i++) {
                                    //此处要考虑双向的情况
                                    _searchSources(sub_sources[i], current_layer + 1, _target_json.children[i]);
                                }

                            }

                            function recoverBiorientation() //恢复最初是否是双向的判断
                            {
                                for (let i = 0; i < total_links_lines.length; i++) {
                                    if ((total_links_lines[i].link_type === possible_cause_type_line) && (total_links_lines[i].is_biorientation === false)) {
                                        total_links_lines[i].is_biorientation = true;
                                    }
                                }
                            }

                            function recoverNodes() {
                                svg_nodes.attr('fill', function (d, i) {
                                    return usual_color;
                                });
                            }

                            function recoverPathsAndLines() {
                                svg_links.style('stroke', function (d, i) {
                                    return usual_path_color;
                                });
                                svg_lines.style('stroke', function (d, i) {
                                    return usual_line_color;
                                });
                            }

                            function recoverText() {
                                span1.text('无');
                                d3.select('div.tree_div').remove();
                                d3.select('div.sub_paths_string_div').remove();
                            }

                            function hilightNodes() // sources
                            {
                                svg_nodes.attr('fill', function (d, i) {
                                    if (sources.indexOf(i) !== -1) {
                                        var in_sources_index = sources.indexOf(i);
                                        var current_source_layer = sources_layer[in_sources_index];
                                        if (d.name === current_clicked_node_name) {
                                            return target_color;
                                        }
                                        var color_layer_num = current_source_layer % cause_layer_color.length;
                                        return cause_layer_color[color_layer_num];
                                    }
                                    else {
                                        return usual_color;
                                    }
                                })
                            }

                            function hilightPathsAndLines() // sources_path, sources_lines
                            {
                                svg_links.style('stroke', function (d, i) {
                                    if (is_cause_path(d.source.index, d.target.index)) {
                                        if (d.link_type === clear_cause_type) {
                                            return clear_path_color;
                                        }
                                        return possible_path_color;
                                    }
                                    return usual_path_color;
                                });

                                svg_lines.style('stroke', function (d, i) {
                                    if (is_cause_line(d.source.index, d.target.index)) {
                                        return possible_line_color;
                                    }
                                    return usual_line_color;
                                })
                            }

                            function is_cause_path(_source, _target) {
                                for (var i = 0; i < sources_path.length; i++) {
                                    var cur_path = sources_path[i];
                                    if (_source === cur_path.source.index && _target === cur_path.target.index) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            function is_cause_line(_source, _target) {
                                for (var i = 0; i < sources_line.length; i++) {
                                    var cur_path = sources_line[i];
                                    if (_source === cur_path.source.index && _target === cur_path.target.index) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            function update_text() {
                                span1.text(current_clicked_node_name);
                                build_target_source_tree(target_source_json_obj);
                                show_paths_string();
                            }

                            function build_target_source_tree(json_data) {
                                //先删除旧的tree svg
                                d3.select('div.tree_div').remove();
                                // d3.select('div.text_div svg').remove();
                                var margin = {top: 100, right: 10, bottom: 240, left: 10},
                                    width = 340 - margin.left - margin.right,
                                    height = 600 - margin.top - margin.bottom;

                                var orientation = {
                                    "bottom-to-top": {
                                        size: [width, height],
                                        x: function (d) {
                                            return d.x
                                        },
                                        y: function (d) {
                                            return height - d.y;
                                        }
                                    }
                                };

                                var tree_div = d3.select('div.text_tree_div').append('div').attr('class','tree_div');
                                var svg = d3.select("div.tree_div").append('svg')
                                // .attr("width", width + margin.left + margin.right)
                                    .attr('width','100%')
                                    .attr('height', height + margin.top + margin.bottom)
                                    .append('g')
                                    .attr('transform', 'translate(' + margin.left + "," + margin.top + ")");

                                var o = orientation["bottom-to-top"];
                                //Compute the layout
                                var treemap = d3.tree().size(o.size);
                                var nodes = d3.hierarchy(json_data);
                                treemap(nodes);
                                var links = nodes.descendants().slice(1);

                                //Create the link lines
                                svg.selectAll(".link").data(links)
                                    .enter().append("path")
                                    .attr("class", "text_link")
                                    .attr("d", function (d) {
                                        return "M" + d.x + "," + o.y(d)
                                            + "C" + d.x + "," + (o.y(d) + o.y(d.parent)) / 2
                                            + " " + d.parent.x + "," + (o.y(d) + o.y(d.parent)) / 2
                                            + " " + d.parent.x + "," + o.y(d.parent);
                                    });

                                //Create the node circles
                                var node = svg.selectAll(".text_node")
                                    .data(nodes.descendants())
                                    .enter()
                                    .append("g");
                                node.append("circle")
                                    .attr("class", "text_node")
                                    .attr("r", 4.5)
                                    .attr("cx", o.x)
                                    .attr("cy", o.y);
                                node.append("text")
                                    .text(function (d) {
                                        return d.data.name;
                                    })
                                    .attr("x", o.x)
                                    .attr("dx", 10)
                                    .attr("dy", -5)
                                    .attr("y", o.y);
                            }

                            function show_paths_string() {
                                build_paths_string();
                                d3.select('div.sub_paths_string_div').remove();
                                var sub_paths_string_div = d3.select('div.paths_string_div').append('div').attr('class', 'sub_paths_string_div');
                                // sub_paths_string_div.append('br');
                                // sub_paths_string_div.append('br');
                                for (var i = 0; i < paths_string.length; i++) {
                                    var _path_string = paths_string[i].split(',').reverse().join(' --> ');
                                    sub_paths_string_div.append('p').text('根因路径' + (i + 1) + ':  ' + _path_string).attr('class', 'paths_string');
                                }
                            }

                            function build_paths_string() {
                                //广度优先构建路径字符串
                                paths_string = [];
                                var cur_node_name = current_clicked_node_name;
                                paths_string.push(cur_node_name);
                                _build_paths_string(target_source_json_obj.children, paths_string[0]);
                            }

                            function _build_paths_string(children, cur_string) {
                                if (children.length == 0) {
                                    return;
                                }
                                var sub_string = [];
                                for (var i = 0; i < children.length; i++) {
                                    sub_string.push(cur_string + ',' + children[i].name);
                                }
                                var cur_string_index_in_paths = paths_string.indexOf(cur_string);
                                paths_string.splice(cur_string_index_in_paths, 1);
                                for (var i = 0; i < sub_string.length; i++) {
                                    paths_string.push(sub_string[i]);
                                    _build_paths_string(children[i].children, sub_string[i]);
                                }
                            }
                        }

                    }
                },
                error: function (msg) {
                    alert('error');
                }
            });

        });
        loadingEffect();
        function loadingEffect() {
            var loading = $('#fountainG');
            loading.hide();
            $(document).ajaxStart(function () {
                loading.show();
            }).ajaxStop(function () {
                loading.hide();
            });
        }

        function cleanShow() {
            d3.select('svg.total_graph').remove();
            d3.select('p.sub_paths_string_div').remove();
            d3.select('div.text_tree_div').remove();
        }

    })
}