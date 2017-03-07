package com.sicao.smartwine.xdata;

import com.sicao.smartwine.SmartCabinetApi;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * RFID数据解析
 */

public class XRfidDataUtil {


    public static HashMap<String,ArrayList<XRfidEntity>> parser(HashMap<String,ArrayList<XRfidEntity>>map , String str) {
        ArrayList<XRfidEntity>current=new ArrayList<>();
        ArrayList<XRfidEntity>add=new ArrayList<>();
        ArrayList<XRfidEntity>remove=new ArrayList<>();
        if (map.containsKey("add")){
            add=map.get("add");
        }
        if (map.containsKey("remove")){
            remove=map.get("remove");
        }
        if (map.containsKey("current")){
            current=map.get("current");
        }
        String s=str.substring(0, 2);
        if (s.contains("a0")||s.contains("b0")||s.contains("c0")){
            while(str.length()>0){
                String start=str.substring(0, 2);
                if(start.equals("a0")){
                    String l=str.substring(2, 4);
                    int lenght=HexToInt(l);
                    str=str.substring(4,str.length());
                    String rfid=str.substring(0, lenght*2);
                    str=str.substring(lenght*2,str.length());
                    XRfidEntity entity=new XRfidEntity();
                    entity.setRfid(rfid);
                    entity.setTag("current");
                    current.add(entity);
                }
                else if(start.equals("b0")){
                    String l=str.substring(2, 4);
                    int lenght=HexToInt(l);
                    str=str.substring(4,str.length());
                    String rfid=str.substring(0, lenght*2);
                    str=str.substring(lenght*2,str.length());
                    XRfidEntity entity=new XRfidEntity();
                    entity.setRfid(rfid);
                    entity.setTag("add");
                    add.add(entity);
                }else if(start.equals("c0")){
                    String l=str.substring(2, 4);
                    int lenght=HexToInt(l);
                    str=str.substring(4,str.length());
                    String rfid=str.substring(0, lenght*2);
                    str=str.substring(lenght*2,str.length());
                    XRfidEntity entity=new XRfidEntity();
                    entity.setRfid(rfid);
                    entity.setTag("remove");
                    remove.add(entity);
                }
            }
            map.put("current",current);
            map.put("add",add);
            map.put("remove",remove);
        }
        return map;
    }
    //16进制转10进制
    public static int HexToInt(String strHex) {
        int nResult = 0;
        if (!IsHex(strHex))
            return nResult;
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (GetHex(ch) * GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //判断是否是16进制数
    public static boolean IsHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))
                continue;
            return false;
        }
        return true;
    }

    //计算16进制对应的数值
    public static int GetHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return (int) (ch - '0');
        if (ch >= 'a' && ch <= 'f')
            return (int) (ch - 'a' + 10);
        if (ch >= 'A' && ch <= 'F')
            return (int) (ch - 'A' + 10);
        throw new Exception("error param");
    }

    //计算幂
    public static int GetPower(int nValue, int nCount) throws Exception {
        if (nCount < 0)
            throw new Exception("nCount can't small than 1!");
        if (nCount == 0)
            return 1;
        int nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }

    //转换成字符串
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
