package com.ad.demo.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by endyc on 2018-05-18.
 */

public class ConverterUtil {
    /**
     * byte array convert GBK string
     *
     * @param bts
     * @return GBK String
     */
    public static String GBKToString(byte[] bts) {
        return GBKToString(bts, 0);
    }

    /**
     * byte array convert GBK string
     *
     * @param bts
     * @param offset
     * @return GBK String
     */
    public static String GBKToString(byte[] bts, int offset) {
        return SGBKToString(bts, offset, bts.length - offset);
    }

    /**
     * byte array convert GBK string
     *
     * @param bts
     * @param offset
     * @param count
     * @return GBK String
     */
    public static String SGBKToString(byte[] bts, int offset, int count) {
        try {
            return new String(bts, offset, count, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Hex string convert byte array
     *
     * @param hex
     * @return byte array
     */
    public static byte[] HexStringToBytes(String hex) {
        hex = hex.replace(" ", "");
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bts;
    }

    /**
     * Byte array convert hex string
     *
     * @param bts
     * @return hex string
     */
    public static String ByteArrayToHexString(byte[] bts) {
        return ByteArrayToHexString(bts, 0, bts.length);
    }

    /**
     * Byte array convert hex string
     *
     * @param bts
     * @param offset
     * @param count
     * @return hex string
     */
    public static String ByteArrayToHexString(byte[] bts, int offset, int count) {
        String ret = "";
        for (int i = offset; i < offset + count; i++) {
            String hex = Integer.toHexString(bts[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }


    /**
     * ascii convert hex string
     *
     * @param str
     * @return HEX string
     */
    public static String StringToHexString(String str) {
        String hexString = "0123456789ABCDEF";
        byte[] bytes = str.getBytes();
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            hex.append(hexString.charAt((bytes[i] & 0xf0) >> 4));  // 作用同 n / 16
            hex.append(hexString.charAt((bytes[i] & 0x0f) >> 0));  // 作用同 n
            hex.append(' ');  //中间用空格隔开
        }
        return hex.toString();
    }

    /**
     * ascii convert byte array
     *
     * @param str
     * @return BYTE数组
     */
    public static byte[] StringToBytes(String str) {
        String hexString = StringToHexString(str);
        return HexStringToBytes(hexString);
    }

    /**
     * 将字符串按照指定长度截取并转存为字符数组，空格忽略。
     *
     * @param strValue 输入字符串
     * @return 数组
     */
    public static String[] StringToStringArray(String strValue, int nLen) {
        String[] strAryResult = null;

        if (strValue != null && !strValue.equals("")) {
            ArrayList<String> strListResult = new ArrayList<String>();
            String strTemp = "";
            int nTemp = 0;

            for (int nloop = 0; nloop < strValue.length(); nloop++) {
                if (strValue.charAt(nloop) == ' ') {
                    continue;
                } else {
                    nTemp++;

                    if (!Pattern.compile("^(([A-F])*([a-f])*(\\d)*)$")
                            .matcher(strValue.substring(nloop, nloop + 1))
                            .matches()) {
                        return strAryResult;
                    }

                    strTemp += strValue.substring(nloop, nloop + 1);

                    //判断是否到达截取长度
                    if ((nTemp == nLen) || (nloop == strValue.length() - 1
                            && (strTemp != null && !strTemp.equals("")))) {
                        strListResult.add(strTemp);
                        nTemp = 0;
                        strTemp = "";
                    }
                }
            }

            if (strListResult.size() > 0) {
                strAryResult = new String[strListResult.size()];
                for (int i = 0; i < strAryResult.length; i++) {
                    strAryResult[i] = strListResult.get(i);
                }
            }
        }

        return strAryResult;
    }

    /**
     * Int convert byte array
     *
     * @param i
     * @return byte array
     */
    public static byte[] IntToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    /**
     * byte array convert int
     *
     * @param bytes
     * @return int
     */
    public static int BytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 25) & 0xFF000000);
        return addr;
    }

    /**
     * byte array convert long
     *
     * @param bytArray
     * @param start
     * @param length
     * @return long
     */
    public static long ByteArrayToDecLong(byte[] bytArray, int start, int length) {
        long reslut = 0;

        for (int i = 0; i < length; i++) {
            reslut += (long) (bytArray[start + i] & 0xff) << (length - i - 1) * 8;
        }
        return reslut;
    }

    /**
     * long convert byte array
     *
     * @param lngdata
     * @param length
     * @return byte array
     */
    public static byte[] ByteArrayToDecLong(long lngdata, int length) {
        byte[] bytArray = new byte[length];
        for (int i = 0; i < length; i++) {
            bytArray[length - 1 - i] = (byte) (0xff & (lngdata >> i * 8));
        }
        return bytArray;
    }

    /**
     * hex string convert wg int
     *
     * @param src
     * @param index
     * @return wg int
     */
    public static long HexStringToWg(String src, int index) {
        byte[] ret = HexStringToBytes(src);
        return bytesToWg26(ret, index);
    }

    /**
     * hex string convert wg string
     *
     * @param src
     * @param index
     * @return wg string
     */
    public static String HexStringToWgString(String src, int index) {
        byte[] bytes = HexStringToBytes(src);
        if (bytes.length < index + 3) return "";
        //int addr = 0;
        int addrh = bytes[index] & 0xFF;
        int addrl = (bytes[index + 2]) & 0xFF;
        addrl |= (bytes[index + 1] << 8 & 0xFF00);
        //addr = addrh * 100000 + addrl;
        return String.valueOf(addrh) + "," + String.format("%05d", addrl);
    }

    /**
     * byte array convert wg int
     *
     * @param bytes
     * @param index
     * @return wg int
     */
    public static long bytesToWg26(byte[] bytes, int index) {
        if (bytes.length < index + 3) return 0;
        long addr = 0;
        long addrh = bytes[index] & 0xFF;
        long addrl = (bytes[index + 2]) & 0xFF;
        addrl |= (bytes[index + 1] << 8 & 0xFF00);
        addr = addrh * 100000 + addrl;
        return addr;
    }

    /**
     * byte array convert wg int
     *
     * @param bytes
     * @param index
     * @return wg int
     */
    public static long bytesToWg34(byte[] bytes, int index) {
        if (bytes.length < index + 4) return 0;
        long addr = 0;
        long addrh = bytes[index + 1] & 0xFF;
        addrh |= (bytes[index] << 8 & 0xFF00);
        long addrl = (bytes[index + 3]) & 0xFF;
        addrl |= (bytes[index + 2] << 8 & 0xFF00);
        addr = addrh * 100000 + addrl;
        return addr;
    }

    /**
     * XOR CheckSum
     *
     * @param bts
     * @param offset
     * @param count
     * @return checksum code
     */
    public static int XOR(byte[] bts, int offset, int count) {
        int inttemp = 0;
        for (int i = offset; i < offset + count; i++) {
            inttemp += bts[i];
            if (inttemp >= 256) inttemp = inttemp - 256;
        }
        return inttemp == 0 ? 0 : 256 - inttemp;
    }

    /**
     * cut part byte array
     *
     * @param bts
     * @param offset
     * @param count
     * @return byte array
     */
    public static byte[] GetData(byte[] bts, int offset, int count) {
        byte[] bytT = new byte[count];

        System.arraycopy(bts, offset, bytT, 0, count);
        return bytT;
    }


    /**
     * byte array convert card no for define type
     *
     * @param type
     * @param idata
     * @param start
     * @param size
     * @return card no
     */
    public static String GetNoForData(int type, byte[] idata, int start, int size) {
        String strno = "";
        switch (type) {
            case 1:
                strno = ByteArrayToHexString(idata, start, 9).toUpperCase();
                strno = strno.replace("A", "X");
                if (RegexUtil.IsIdCard(strno))
                    return strno;
                return "";
            case 2:
                strno = ByteArrayToHexString(idata, start, size).toUpperCase();
                strno = strno.replace("B", "");
                if (RegexUtil.IsNumber(strno))
                    return strno;
                return "";
            case 3:
                strno = String.valueOf(bytesToWg26(idata, start));
                if (RegexUtil.IsDec(strno))
                    return strno;
                return "";
            default:
                strno = SGBKToString(idata, start, 8);
                if (RegexUtil.IsCarNo(strno))
                    return strno;
                return "";
        }
    }

    public static String GetTagValueForDataArray(byte[] dataarray, int start, int len, int ptype, int pStart, int pLen) {
        String ret = "";
        switch (ptype) {
            case 0:
                if (pLen > 8) return "error byte, Less than 8";
                if (len - pStart < pLen) {
                    return String.valueOf(ConverterUtil.ByteArrayToDecLong(dataarray, start + pStart, len - pStart));
                } else {
                    return String.valueOf(ConverterUtil.ByteArrayToDecLong(dataarray, start + pStart, pLen));
                }
            case 1:
                if (len - pStart < pLen)
                    return ConverterUtil.ByteArrayToHexString(dataarray, start + pStart, len - pStart);
                else
                    return ConverterUtil.ByteArrayToHexString(dataarray, start + pStart, pLen);
            case 2:
                if (pLen > 4 || pLen < 3) return "error Byte,just 3 or 4";
                if (len - pStart < pLen)
                    return "have not more data,need change start";
                else if (pLen == 4) {
                    return String.valueOf(ConverterUtil.bytesToWg34(dataarray, start + pStart));
                } else if (pLen == 3) {
                    return String.valueOf(ConverterUtil.bytesToWg26(dataarray, start + pStart));
                } else {
                    return "Other error";
                }
            default:
                return "not support type";
        }
    }

    public static String GetTagValueForHexString(String hexString, int ptype, int pStart, int pLen) {
        byte[] dataarray = ConverterUtil.HexStringToBytes(hexString);
        return GetTagValueForDataArray(dataarray, 0, dataarray.length, ptype, pStart, pLen);
    }
}
