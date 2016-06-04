package com.test.www.test;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Administrator on 2016/6/4.
 */
public class MyHandler extends DefaultHandler {
    public static final String TAG = "MyHandler";
    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;
    /**
     * 开始 XML 解析的时候调用
     * @throws SAXException
     */
    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    /**
     * 开始解析某个结点的时候调用
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 记录当前结点名
        nodeName = localName;
    }

    /**
     * 在获取结点中内容的时候调用
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 根据当前的结点名判断将内容添加到哪一个StringBuilder对象中
        if ("id".equals(nodeName)) {
            id.append(ch, start, length);
        } else if ("name".equals(nodeName)) {
            name.append(ch, start, length);
        } else if ("version".equals(nodeName)) {
            version.append(ch, start, length);
        }
    }

    /**
     * 完成解析某个结点的时候调用
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)) {
            Log.d(TAG, "id is " + id.toString().trim());
            Log.d(TAG, "name is " + name.toString().trim());
            Log.d(TAG, "version is " + version.toString().trim());
            // 最后要将StringBuilder清空掉
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    /**
     * 完成整个 XML 解析的时候调用
     * @throws SAXException
     */
    @Override
    public void endDocument() throws SAXException {
    }
}
