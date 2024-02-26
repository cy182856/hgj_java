package com.ej.hgj.service.wechat;

import org.dom4j.DocumentException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface WechatService {

     String handleMessage(HttpServletRequest request) throws IOException, DocumentException, JAXBException;
}
