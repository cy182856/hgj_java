package com.ej.hgj.utils.wechat;

import com.ej.hgj.request.RespTextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

public class MessageUtil {

    public static String parse2Xml(HttpServletRequest request) throws IOException{
    	// 从request中取得输入流  
        InputStream inputStream = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null; 
        try{
        	while((line = reader.readLine()) != null){
        		sb.append(line);
        	}
        }catch(IOException e){
        	e.printStackTrace();
        }finally{
        	try{
        		inputStream.close();
        	}catch(IOException e){
        		e.printStackTrace();
        	}
        }
    	return sb.toString();
    }

    /**
     * 解析返回xml报文
     * @param result
     * @param clazz
     * @param parentClazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, V> T convertResult(String result, Class<T> clazz, Class<V> parentClazz) {
        XStream xStream = new XStream(new DomDriver());
        xStream.addDefaultImplementation(clazz, parentClazz);
        xStream.autodetectAnnotations(true);
        xStream.processAnnotations(clazz);
        Object transaction = xStream.fromXML(result);
        return (T)transaction;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @return xml
     */
    public static String messageToXml(RespTextMessage respTextMessage) {
        xstream.alias("xml", respTextMessage.getClass());
        return xstream.toXML(respTextMessage);
    }

    /**
     * 扩展xstream，使其支持CDATA块
     *
     * @date 2013-05-19
     */
    @SuppressWarnings("unused")
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });
}  