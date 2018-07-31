package com.test.testh264sender.http;

import com.harsom.baselib.net2.BaseResponse;

/**
 * 使用的是阿里云对象存储 OSS(Object Storage Service)
 * 上传参数请求结果
 * Created by Yuri on 2016/6/13.
 */
public class UploadParamResponse extends BaseResponse {
    //各字段定义详见 https://help.aliyun.com/document_detail/31827.html?spm=5176.doc31826.6.138.ue8apS
    /**OSS对外服务的访问域名*/
    public String endpoint;
    /**访问密钥*/
    public String accessKey;
    public String secretKey;
    public String securityToken;
    public String bucketName;
    public String objectKey;

    @Override
    public String toString() {
        return "UploadParamResponse{" +
                "endpoint='" + endpoint + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", securityToken='" + securityToken + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", objectKey='" + objectKey + '\'' +
                '}';
    }
}
