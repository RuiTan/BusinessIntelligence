<template>
    <el-container>
        <div id="graph" style="width:100%;height:100%;">

        </div>
    </el-container>

</template>

<style scoped>

</style>

<script>
    import Neo4JD3 from 'neo4jd3'
    var neo4jd3 = new Neo4JD3('#neo4jd3', {
        highlight: [{
            class: 'Project',
            property: 'name',
            value: 'neo4jd3'
        }, {
            class: 'User',
            property: 'userId',
            value: 'eisman'
        }],
        icons: {
            'Api': 'gear',
            'BirthDate': 'birthday-cake',
            'Cookie': 'paw',
            'Email': 'at',
            'Git': 'git',
            'Github': 'github',
            'Ip': 'map-marker',
            'Issues': 'exclamation-circle',
            'Language': 'language',
            'Options': 'sliders',
            'Password': 'asterisk',
            'Phone': 'phone',
            'Project': 'folder-open',
            'SecurityChallengeAnswer': 'commenting',
            'User': 'user',
            'zoomFit': 'arrows-alt',
            'zoomIn': 'search-plus',
            'zoomOut': 'search-minus'
        },
        images: {
            'Address': 'img/twemoji/1f3e0.svg',
            'BirthDate': 'img/twemoji/1f5d3.svg',
            'Cookie': 'img/twemoji/1f36a.svg',
            'CreditCard': 'img/twemoji/1f4b3.svg',
            'Device': 'img/twemoji/1f4bb.svg',
            'Email': 'img/twemoji/2709.svg',
            'Git': 'img/twemoji/1f5c3.svg',
            'Github': 'img/twemoji/1f5c4.svg',
            'icons': 'img/twemoji/1f38f.svg',
            'Ip': 'img/twemoji/1f4cd.svg',
            'Issues': 'img/twemoji/1f4a9.svg',
            'Language': 'img/twemoji/1f1f1-1f1f7.svg',
            'Options': 'img/twemoji/2699.svg',
            'Password': 'img/twemoji/1f511.svg',
            'Project|name|d3': 'img/twemoji/32-20e3.svg',
            'Project|name|neo4j': 'img/twemoji/33-20e3.svg',
            'Project|name|neo4jd3': 'img/twemoji/31-20e3.svg',
            'User': 'img/twemoji/1f600.svg'
        },
        minCollision: 60,
        neo4jDataUrl: 'json/neo4jData.json',
        nodeRadius: 25,
        onNodeDoubleClick: function (node) {
            switch (node.id) {
                case '25':
                    // Google
                    window.open(node.properties.url, '_blank');
                    break;
                default:
                    var maxNodes = 5,
                        data = neo4jd3.randomD3Data(node, maxNodes);
                    neo4jd3.updateWithD3Data(data);
                    break;
            }
        },
        zoomFit: true
    });
    export default {
        name: 'neo4j',
    }
</script>