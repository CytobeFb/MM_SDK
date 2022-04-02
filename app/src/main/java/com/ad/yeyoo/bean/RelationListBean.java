package com.ad.yeyoo.bean;

import java.util.List;

public class RelationListBean {

    /**
     * data : [{"kuanhao":"21W1194HY","labelId":"000008000000000000000000","color":"黑色","guige":"M","rowNumber":1,"tagName":"标签1","productName":"2*2螺纹V领弹力衫"}]
     * success : true
     */

    private boolean success;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * kuanhao : 21W1194HY
         * labelId : 000008000000000000000000
         * color : 黑色
         * guige : M
         * rowNumber : 1
         * tagName : 标签1
         * productName : 2*2螺纹V领弹力衫
         */

        private String kuanhao;
        private String labelId;
        private String color;
        private String guige;
        private Integer rowNumber;
        private String tagName;
        private String productName;
        private String custName;
        private Integer goodsNum;

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public Integer getGoodsNum() {
            return goodsNum;
        }

        public void setGoodsNum(Integer goodsNum) {
            this.goodsNum = goodsNum;
        }

        public String getKuanhao() {
            return kuanhao;
        }

        public void setKuanhao(String kuanhao) {
            this.kuanhao = kuanhao;
        }

        public String getLabelId() {
            return labelId;
        }

        public void setLabelId(String labelId) {
            this.labelId = labelId;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getGuige() {
            return guige;
        }

        public void setGuige(String guige) {
            this.guige = guige;
        }

        public Integer getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(Integer rowNumber) {
            this.rowNumber = rowNumber;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }
}
