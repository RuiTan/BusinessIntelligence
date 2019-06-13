FROM nginx:1.13.7
# 使用Nginx

MAINTAINER LeonLiang <leonnnop@icloud.com> 

RUN rm /etc/nginx/conf.d/default.conf 
# 删除nginx 默认配置

ADD default.conf /etc/nginx/conf.d/ 
# 添加我们自己的配置 default.conf 在下面

COPY dist/  /usr/share/nginx/html/  
# 把刚才生成dist文件夹下的文件copy到nginx下面去

RUN /bin/bash -c 'echo init ok!!!'

