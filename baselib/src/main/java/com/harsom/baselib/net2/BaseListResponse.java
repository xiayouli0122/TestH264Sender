package com.harsom.baselib.net2;

/**
 * 列表数据返回的父类
 * 包含一个totalCount + pageIndex
 * Created by Yuri on 2017/6/16.
 */

public class BaseListResponse extends BaseResponse{
    /**
     * 结果总条数
     */
    public int totalCount;
    /**
     * 当前分页数
     */
    public int pageIndex;
}
