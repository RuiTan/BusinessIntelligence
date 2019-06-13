<template>
    <el-container style="width:100%">

        <el-col>
            <el-row v-for="button in buttons" :key="button.name">
                <el-button type="info" plain style="margin:20px;" @click="labelClick(button.name)">{{button.name}}</el-button>
            </el-row>
        </el-col>

        <el-col>
            <el-row>
                <div id="alchemy" class="alchemy" style="width:1520px;height:630px;"></div>
            </el-row>

            <el-row style="margin-top:20px;padding-bottom:30px;">
                <el-tag type="warning"> {{this.propertyType}}</el-tag>
                <span style="margin-left:10px;" v-for="propertyText in propertyTexts" :key="propertyText.id">
                    <span style="font-weight:bold;">{{propertyText.key}}: </span>
                 
                    <span>{{propertyText.value}}</span>
                </span>
            </el-row>
        </el-col>


    </el-container>
</template>

<style scoped>
    /* @import "/public/0.4.2/alchemy.css" */
</style>

<script>
    let axios = require('axios')
    var some_data = {
        "nodes": [{
                "id": 1
            },
            {
                "id": 2
            },
            {
                "id": 3
            }
        ],
        "edges": [{
                "source": 1,
                "target": 2
            },
            {
                "source": 1,
                "target": 3,
            }
        ]
    };

    export default {
        name: 'graph',
        data() {
            return {
                colors: ["#DD79FF", "#00FF30", "#5168FF", "#f83f00", "#ff8d8f", "#45b787", "#a7535a", "#ec8aa4"],
                nodeTypes: {},
                nodeStyles: {},
                labels: [],
                refresh: false,
                alchemy: '',
                buttons: [],
                propertyType: 'Container_Node',
                propertyTexts: [
                    {
                        id: 0,
                        key: 'test',
                        value: 'test'
                    },
                    {
                        id: 1,
                        key: 'test',
                        value: 'test'
                    },
                    {
                        id: 2,
                        key: 'test',
                        value: 'test'
                    },
                ],
            }
        },
        methods: {
            labelClick(labelName) {
                let baseURL = ''
                if (labelName == 'All_Nodes') {
                    baseURL = 'http://10.60.38.173:9999/api/getAll'
                } else {
                    baseURL = 'http://10.60.38.173:9999/api/getLabel?label=' + labelName;
                }
                axios.get(baseURL)
                    .then((response) => {
                        this.alchemyStart(response);
                    })
            },
            setPropertyText(type, performance) {
                console.log(type)
                console.log(performance)

                this.propertyType = type;

                let tempText = 'type:test;property:test';
                let tempTexts = [];
                let index = 0;
                
                performance.split(";").forEach(element => {
                    console.log(element)
                    console.log(element.split(":")[0])
                    tempTexts.push({
                        id: index,
                        key: element.split(":")[0],
                        value: element.split(":")[1],
                    })
                    index = index + 1;
                });

                this.propertyTexts = tempTexts;
                console.log(this.propertyTexts)
            },
            alchemyStart(response) {
                let nodes = response.data.nodes;
                let edges = response.data.links;

                let data = {
                    nodes: nodes,
                    edges: edges,
                }

                console.log(data)

                this.alchemy.begin({
                    // "dataSource": require('../assets/example.json')
                    "dataSource": data,
                    "nodeCaption": function (node) {
                        return node.name;
                    },
                    nodeCaptionsOnByDefault: true,
                    edgeCaption: function (node) {
                        return node.type;
                    },
                    forceLocked: false,
                    "linkDistance": function () {
                        return 40;
                    },
                    nodeTypes: this.nodeTypes,
                    nodeStyle: this.nodeStyles,
                    nodeMouseOver: (node) => {
                        console.log(this.colors);
                        console.log(node)
                        this.setPropertyText(node._properties.type, node._properties.performance);
                        return 'node.performance';
                    }
                });
            }
        },
        mounted() {
            this.refresh = true;
            this.alchemy = alchemy;

            this.propertyType = '';
            this.propertyTexts = [];

            // axios.get('/example.json')
            axios.get('http://10.60.38.173:9999/api/getAll')
                .then((response) => {
                    console.log(response.data)
                    // let nodes = response.data.nodes;
                    // let edges = response.data.links;

                    // let data = {
                    //     nodes: nodes,
                    //     edges: edges
                    // }

                    // console.log(data)

                    // this.alchemy.begin({
                    //     // "dataSource": require('../assets/example.json')
                    //     "dataSource": data,
                    //     "nodeCaption": function (node) {
                    //         return node.name;
                    //     },
                    //     nodeCaptionsOnByDefault: true,
                    //     edgeCaption: function (node) {
                    //         return node.type;
                    //     },
                    //     forceLocked: false,
                    //     "linkDistance": function () {
                    //         return 40;
                    //     },

                    // });

                    let rawResponse = response;
                    axios.get('http://10.60.38.173:9999/api/getAllLabel')
                        .then((response) => {
                            this.labels = response.data.Label;
                            this.buttons = [];
                            let index = 0;

                            this.buttons.push({
                                name: 'All_Nodes'
                            });

                            this.labels.forEach(element => {
                                this.buttons.push({
                                    name: element
                                });

                                this.nodeStyles[element] = {
                                    "color": this.colors[index],
                                }

                                index = index + 1;
                            });

                            this.nodeTypes.type = this.labels;

                            console.log(this.nodeTypes)
                            console.log(this.nodeStyles)

                            this.alchemyStart(rawResponse);
                        })

                })



        }
    }
</script>