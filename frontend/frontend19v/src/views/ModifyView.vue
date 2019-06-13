<template>
    <el-container style="margin:20px;">
        <el-card class="box-card" v-for="card in cards" :key="card.name" style="margin-right:25px;">
            <div slot="header" class="clearfix">
                <span>{{card.name}}
                </span>
                <el-button style="float: right; padding: 3px 0" type="text" @click="handleUpload(card.type)">{{card.uploadText}}</el-button>
            </div>
            <div class="text item">
                {{card.escri}}
            </div>
        </el-card>

        <el-dialog title="上传文件" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
            <el-upload class="upload-demo" ref="upload" action="https://jsonplaceholder.typicode.com/posts/"
                :on-preview="handlePreview" :on-remove="handleRemove" :file-list="fileList" :auto-upload="false"
                :accept="fileType" :on-progress="uploadOnProgress">
                <!-- <el-row> -->
                <el-button slot="trigger" size="small" plain type="primary">选取文件</el-button>
                <el-button style="margin-left: 15px;" size="small" type="success" plain @click="submitUpload">上传到服务器</el-button>
                <!-- </el-row> -->
                <el-row style="width:40%;margin: 15px 0;">
                    <el-input v-model="systemNameYaml" placeholder="请输入系统名称" v-if="isYaml" size="small"></el-input>
                    <el-select v-model="systemNameCSV" placeholder="请选择系统名称" v-if="!isYaml" size="small">
                        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-row>
                <div slot="tip" class="el-upload__tip" style="margin-bottom:15px;">只能上传{{this.fileType}}文件，且不超过500kb</div>
            </el-upload>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="输入地址" :visible.sync="dialogURLVisible" width="30%" :before-close="handleClose">
            <!-- <el-input placeholder="请输入内容" v-model="inputURL" style="margin: 15px 0px;">
                <template slot="prepend">http://</template>
            </el-input> -->
            <el-select v-model="selectedElement" placeholder="请选择element">
                <el-option v-for="item in elements" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>

            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogURLVisible = false">取 消</el-button>
                <el-button type="primary" @click="URLConfirmClick">确 定</el-button>
            </span>
        </el-dialog>
    </el-container>
</template>

<style>
    input.el-upload__input {
        visibility: hidden;
        position: absolute;
    }

    .text {
        font-size: 14px;
    }

    .item {
        margin-bottom: 15px;
    }

    .input-with-select .el-input-group__prepend {
        background-color: #fff;
    }

    .clearfix:before,
    .clearfix:after {
        display: table;
        content: "";
    }

    .clearfix:after {
        clear: both
    }

    .box-card {
        width: 300px;
    }
</style>

<script>
    let axios = require('axios')
    export default {
        name: 'modify',
        data() {
            return {
                selectedElement: '',
                elements: [],
                systemNameYaml: '',
                systemNameCSV: '',
                options: [{
                    value: '选项1',
                    label: '黄金糕'
                }, {
                    value: '选项2',
                    label: '双皮奶'
                }, {
                    value: '选项3',
                    label: '蚵仔煎'
                }, {
                    value: '选项4',
                    label: '龙须面'
                }, {
                    value: '选项5',
                    label: '北京烤鸭'
                }],
                isYaml: true,
                fileName: '',
                inputURL: '',
                dialogVisible: false,
                dialogURLVisible: false,
                fileList: [
                    //     {
                    //     name: 'food.jpeg',
                    //     url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
                    // }, {
                    //     name: 'food2.jpeg',
                    //     url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
                    // }
                ],
                cards: [{
                        name: '上传yaml部署文件',
                        type: '.yaml',
                        escri: '上传部署配置文件，系统将自动提取节点间的关系。',
                        uploadText: '上传文件'
                    },
                    {
                        name: '上传json服务调用关系文件',
                        type: '.json',
                        escri: '上传服务调用关系文件，系统将自动提取节点间的关系。',
                        uploadText: '上传文件'
                    },
                    {
                        name: '获取目标系统部署数据',
                        type: '.pod',
                        escri: '获取目标系统部署数据，系统将自动提取pods与节点间的关系。',
                        uploadText: '输入地址'
                    },
                    {
                        name: '上传csv数据集文件',
                        type: '.csv',
                        escri: '输入csv数据集，系统将自动提取性能与数据集列间的关系。',
                        uploadText: '上传文件'
                    },
                ],
                fileType: '.yaml'

            };
        },
        methods: {
            URLConfirmClick() {
                this.dialogURLVisible = false;
                axios.post(
                        'http://10.60.38.173:9999/api/addNods?Url=http://10.60.38.182:5525/tool/api/v1.0/get_pods/' +
                        this.selectedElement)
                    .then((response) => {
                        console.log(response.data);
                        this.$message({
                            message: 'Nodes信息输入成功！',
                            type: 'success'
                        });
                    })
            },
            uploadOnProgress(e, file, fileList) { //开始上传

                console.log('file', file)
                console.log(fileList)


                if (this.fileName == file.name) {
                    return;
                } else {
                    this.fileName = file.name;
                }

                var oneFile = file.raw;
                var formdata = new FormData(); // 创建form对象
                formdata.append('file', oneFile);

                let config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }; //添加请求头

                let uploadURL = 'http://10.60.38.173:9999/api/yamldeal';

                console.log(this.fileType)

                if (this.fileType == '.yaml') {
                    uploadURL = 'http://10.60.38.173:9999/api/yamldeal?systemName=' + this.systemNameYaml;
                    console.log('yaml')
                } else if (this.fileType == '.csv') { // .csv
                    uploadURL = 'http://10.60.38.173:9999/api/fileupdate?systemName=' + this.systemNameCSV;
                    console.log('csv')
                } else {
                    uploadURL = 'http://10.60.38.173:9999/api/addServiceRelation?systemName=' + this.systemNameCSV;
                    console.log('json')
                }

                console.log(uploadURL)
                axios.post(uploadURL,
                    formdata,
                    config).then((response) => { //这里的/xapi/upimage为接口
                    console.log(response.data);
                    this.$message({
                        message: '文件上传成功！',
                        type: 'success'
                    });
                })
            },
            handleUpload(fileType) {
                this.fileType = fileType;
                console.log(fileType)
                if (this.fileType == '.yaml') {
                    this.isYaml = true;
                } else {
                    this.isYaml = false;
                }
                if (fileType == '.pod') {
                    this.dialogURLVisible = true
                } else {
                    this.dialogVisible = true
                }
            },
            handleClose(done) {
                this.$confirm('确认关闭？')
                    .then(_ => {
                        done();
                    })
                    .catch(_ => {});
            },
            submitUpload() {
                this.$refs.upload.submit();
            },
            handleRemove(file, fileList) {
                console.log(file, fileList);
            },
            handlePreview(file) {
                console.log(file);
            }
        },
        created() {
            axios.get('http://10.60.38.173:9999/api/getElementName')
                .then((response) => {
                    let element = response.data.Element;
                    element.forEach(element => {
                        this.elements.push({
                            label: element,
                            value: element
                        })
                    });
                })
            axios.get('http://10.60.38.173:9999/api/getSystem')
                .then((response) => {
                    this.options = [];
                    response.data.nodes.forEach(element => {
                        this.options.push({
                            value: element.name,
                            label: element.name,
                        })
                    });
                })
        }
    }
</script>