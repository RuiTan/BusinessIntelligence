<template>
    <el-container v-loading.fullscreen.lock="loading">
        <el-row style="width:100%;height:100%">
            <el-col :span="6">
                <el-row style="margin-left:10%;margin-top:20px;">
                    <el-select v-model="datasetName" placeholder="请选择Dataset" @change="onDatasetChange">
                        <el-option v-for="item in datasets" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-row>


                <el-col class="box2" style="width:350px; height:700px;">
                    <el-col style="width:350px;height:650px; overflow-y:scroll; overflow-x:hidden;">
                        <!-- <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox> -->
                        <!-- <div style="margin: 15px 0;"></div> -->
                        <!-- <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange" :min="0" :max="1">
                            <el-checkbox v-for="city in cities" :label="city" :key="city" style="margin-top:15px;">{{city}}</el-checkbox>
                        </el-checkbox-group> -->
                        <el-radio-group v-model="checkedCities" @change="handleCheckedCitiesChange">
                            <el-radio v-for="city in cities" :label="city" :key="city" style="margin-top:15px;margin-left:0px;">{{city}}</el-radio>
                        </el-radio-group>
                    </el-col>
                </el-col>
            </el-col>

            <el-col style="height:600px;margin-left:8%;margin-top:30px;" :span="16">
                <div refs="chart" id="chart" style="width:100%;height:100%"></div>
            </el-col>
        </el-row>

    </el-container>
</template>

<style scoped>
    svg {
        left: 50%;
    }

    .el-checkbox+.el-checkbox {
        margin-left: 0px;
    }

    .box2 {
        margin-top: 30px;
        margin-left: 50px;
        /* margin: 20px auto; */
        width: 200px;
        min-height: 500px;
        padding: 10px;
        position: relative;
        background: -webkit-gradient(linear, 0% 20%, 0% 92%, from(#c7ecf59a), to(rgb(236, 246, 248)), color-stop(.1, #c7ecf59a));
        border-top: 1px solid #ccc;
        border-right: 1px solid #ccc;
        -webkit-border-bottom-right-radius: 60px 60px;
        -webkit-box-shadow: -1px 2px 2px rgba(0, 0, 0, 0.2);
    }

    .box2:before {
        content: '';
        width: 25px;
        height: 20px;
        position: absolute;
        bottom: 0;
        right: 0;
        -webkit-border-bottom-right-radius: 30px;
        -webkit-box-shadow: -2px -2px 5px rgba(0, 0, 0, 0.3);
        -webkit-transform:
            rotate(-20deg) skew(-40deg, -3deg) translate(-13px, -13px);
    }
</style>

<script>
    const echarts = require('echarts');
    const axios = require('axios');
    // var cityOptions = ['Data1', 'Data2', 'Data3', 'Data4', 'Data5'];

    var option = {
        title: {
            text: 'TestDiagram'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['Data1', 'Data2', 'Data3', 'Data4', 'Data5']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['Data1', 'Data2', 'Data3', 'Data4', 'Data5', 'Data6', 'Data7']
        },
        yAxis: {
            type: 'value'
        },
        series: [{
                name: 'Data1',
                type: 'line',
                stack: '总量',
                data: [120, 132, 101, 134, 90, 230, 210]
            },
            {
                name: 'Data2',
                type: 'line',
                stack: '总量',
                data: [220, 182, 191, 234, 290, 330, 310]
            },
            {
                name: 'Data3',
                type: 'line',
                stack: '总量',
                data: [150, 232, 201, 154, 190, 330, 410]
            },
            {
                name: 'Data4',
                type: 'line',
                stack: '总量',
                data: [320, 332, 301, 334, 390, 330, 320]
            },
            {
                name: 'Data5',
                type: 'line',
                stack: '总量',
                data: [820, 932, 901, 934, 1290, 1330, 1320]
            }
        ]
    };
    export default {
        name: 'series',
        data() {
            return {
                loading: true,
                datasetName: '',
                datasets: [{
                    label: 'example.csv',
                    value: 'example.csv'
                }],
                checkAll: false,
                checkedCities: ['Data1', 'Data2', 'Data3', 'Data4', 'Data5'],
                cities: ['Data1', 'Data2', 'Data3', 'Data4', 'Data5'],
                isIndeterminate: false,
                myChart: '',
                series: [{
                        name: 'Data1',
                        type: 'line',
                        stack: '总量',
                        data: [120, 132, 101, 134, 90, 230, 210]
                    },
                    {
                        name: 'Data2',
                        type: 'line',
                        stack: '总量',
                        data: [220, 182, 191, 234, 290, 330, 310]
                    },
                    {
                        name: 'Data3',
                        type: 'line',
                        stack: '总量',
                        data: [150, 232, 201, 154, 190, 330, 410]
                    },
                    {
                        name: 'Data4',
                        type: 'line',
                        stack: '总量',
                        data: [320, 332, 301, 334, 390, 330, 320]
                    },
                    {
                        name: 'Data5',
                        type: 'line',
                        stack: '总量',
                        data: [820, 932, 901, 934, 1290, 1330, 1320]
                    }
                ]
            };
        },
        methods: {
            onDatasetChange(value) {
                this.loading = true;
                let initData = () => {
                    return axios.get('http://10.60.38.173:9999/api/getOperationData?filename=' + this.datasetName);
                }

                let initTimeStamp = () => {
                    return axios.get('http://10.60.38.173:9999/api/getTimestamp?filename=' + this.datasetName);
                }

                // 获取数据集后，再申请数据。
                axios.all([initData(), initTimeStamp()])
                    .then(axios.spread((responseData, responseStamp) => {
                        console.log(responseData.data)
                        // responseData.data = JSON.parse(responseData.data)
                        let yData = responseData.data.Operations;
                        let yCData = [];
                        this.checkedCities = [];
                        this.cities = [];

                        for (const key in yData) {
                            var tempElement = {}
                            if (yData.hasOwnProperty(key)) {
                                const element = yData[key];
                                tempElement.name = key;
                                // tempElement.name = tempElement.name.substring(15);
                                tempElement.data = element;

                                yCData.push(tempElement)
                                // this.checkedCities.push(tempElement.name);
                                this.cities.push(tempElement.name);
                            }
                        }
                        console.log(yData)
                        console.log(yCData)
                        yCData.forEach(element => {
                            element.type = 'line';
                            element.stack = '总量';
                        });

                        option.xAxis.data = responseStamp.data.TimeStamp;

                        option.series = yCData;
                        this.series = yCData;
                        this.myChart.setOption(option, {
                            notMerge: true
                        });

                        this.loading = false;
                    }))
            },
            chartUpdate(value) {
                let currentSeries = []
                console.log(value)
                console.log('series', this.series)
                this.series.forEach(serie => {
                    // serie.forEach(value => {
                    if (serie.name == value) {
                        currentSeries.push(serie)
                    }
                    // });
                });

                option.series = currentSeries;

                this.myChart.setOption(option, {
                    notMerge: true
                });
            },
            handleCheckAllChange(val) {
                this.checkedCities = val ? this.cities : [];
                this.isIndeterminate = false;

                this.chartUpdate(this.checkedCities);
            },
            handleCheckedCitiesChange(inValue) {
                // console.log(inValue)
                let value = [].push(inValue);
                let checkedCount = value.length;
                this.checkAll = checkedCount === this.cities.length;
                this.isIndeterminate = checkedCount > 0 && checkedCount < this.cities.length;

                this.chartUpdate(inValue);
                console.log('checkedcities')
                console.log(this.checkedCities)
            }
        },
        mounted() {
            this.checkAll = true;
            this.myChart = echarts.init(document.getElementById('chart'));
            // var myChart = echarts.init(this.$refs.chart);

            this.myChart.setOption(option);

            axios.get('http://10.60.38.173:9999/api/getCSV').then((response) => {
                let data = response.data.CSV;
                this.datasets = []

                data.forEach(element => {
                    this.datasets.push({
                        label: element,
                        value: element
                    })
                });
                this.datasetName = this.datasets[0].label;

                let initData = () => {
                    return axios.get('http://10.60.38.173:9999/api/getOperationData?filename=' + this.datasets[
                        0].label);
                }

                let initTimeStamp = () => {
                    return axios.get('http://10.60.38.173:9999/api/getTimestamp?filename=' + this.datasets[
                        0].label);
                }

                // 获取数据集后，再申请数据。
                axios.all([initData(), initTimeStamp()])
                    .then(axios.spread(
                        (responseData, responseStamp) => {
                            // console.log(responseData.data)
                            // responseData.data = JSON.parse(responseData.data)
                            let yData = responseData.data.Operations;
                            let yCData = [];
                            this.checkedCities = [];
                            this.cities = [];

                            for (const key in yData) {
                                var tempElement = {}
                                if (yData.hasOwnProperty(key)) {
                                    const element = yData[key];
                                    tempElement.name = key;
                                    // tempElement.name = tempElement.name.substring(15);
                                    tempElement.data = element;

                                    yCData.push(tempElement)
                                    // this.checkedCities.push(tempElement.name);
                                    this.cities.push(tempElement.name);
                                }
                            }
                            // console.log(yData)
                            // console.log(yCData)
                            yCData.forEach(element => {
                                element.type = 'line';
                                element.stack = '总量';
                            });

                            option.xAxis.data = responseStamp.data.TimeStamp;

                            option.series = yCData;
                            this.series = yCData;
                            this.myChart.setOption(option, {
                                notMerge: true
                            });

                            this.loading = false;
                        }))
            })






        }
    }
</script>