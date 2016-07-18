package com.longway.sample;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by longway
 */

public class Xml {
    private static final String TAG = Xml.class.getSimpleName();
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String[] FILTER_PACKAGE = {"java", "android"};

    public Xml() {

    }

    private static boolean isList(Class<?> klass) {
        if (klass == ArrayList.class || klass == HashSet.class) {
            return true;
        }
        return false;
    }

    private static boolean isMap(Class<?> klass) {
        if (klass == HashMap.class || klass == ConcurrentHashMap.class) {
            return true;
        }
        return false;
    }

    private static class InputSource<T extends InputStream & Closeable> {
        public static final String DEFAULT_ENCODING = "UTF-8";
        private T mSource;
        private String mEncoding = DEFAULT_ENCODING;

        public static InputStream getInputStream(byte[] source) {
            return new BufferedInputStream(new ByteArrayInputStream(source));
        }

        public static InputStream getInputStream(File source) throws FileNotFoundException {
            return new BufferedInputStream(new FileInputStream(source));
        }

        public static InputSource getInputSource(byte[] source) {
            return new InputSource(getInputStream(source));
        }


        public InputSource(T source, String encoding) {
            this.mSource = source;
            this.mEncoding = encoding;
        }

        public InputSource(T source) {
            this.mSource = source;
        }

        public T getSource() {
            return mSource;
        }

        public void setSource(T mSource) {
            this.mSource = mSource;
        }

        public String getEncoding() {
            return mEncoding;
        }

        public void setEncoding(String mEncoding) {
            this.mEncoding = mEncoding;
        }
    }

    private static XmlPullParser getParser(InputSource inputSource) throws XmlPullParserException, IOException {
        XmlPullParser parser = android.util.Xml.newPullParser();
        parser.setInput(inputSource.getSource(), inputSource.getEncoding());
        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
        }
        if (type != XmlPullParser.START_TAG) {
            throw new InflateException(parser.getPositionDescription()
                    + ": No start tag found!");
        }
        return parser;
    }

    public <T> T fromXml(Type type, String xml) {
        return fromXml(type, InputSource.getInputSource(xml.getBytes()));
    }

    public <T> T fromXml(Type type, CharSequence xmlPath) {
        try {
            return fromXml(type, InputSource.getInputStream(new File(xmlPath.toString())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T fromXml(Type type, File xml) {
        try {
            return fromXml(type, InputSource.getInputStream(xml));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T fromXml(Type type, InputStream xml) {
        return fromXml(type, new InputSource(xml));
    }

    public <T> T fromXml(Type type, byte[] xml) {
        return fromXml(type, InputSource.getInputSource(xml));
    }

    public <T> T fromXml(Type type, ByteBuffer xml) {
        xml.flip();
        return fromXml(type, InputSource.getInputSource(xml.array()));
    }

    public <T> T fromXml(Type type, InputSource xml) {
        TypeToken typeToken = TypeToken.getTypeToken(type);
        Class<?> rawType = typeToken.getRawType();
        Class<?> clz = typeToken.getActualType();
        if (isList(rawType)) {
            try {
                return (T) createList(getParser(xml), rawType, clz);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isMap(rawType)) {
            try {
                return (T) createMap(getParser(xml), rawType);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return fromXml((Class<? extends T>) clz, xml.getSource());
        }
        return null;
    }

    public <T> T fromXml(Class<T> klass, ByteBuffer xml) {
        xml.flip();
        return fromXml(klass, xml.array());
    }

    public <T> T fromXml(Class<T> klass, byte[] xml) {
        return fromXml(klass, new ByteArrayInputStream(xml));
    }

    public <T> T fromXml(Class<T> klass, File xml) {
        try {
            return fromXml(klass, new BufferedInputStream(new FileInputStream(xml)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T fromXml(Class<T> klass, CharSequence xmlPath) {
        return fromXml(klass, new File(xmlPath.toString()));
    }

    public <T> T fromXml(Class<T> klass, InputStream xml) {
        try {
            return fromXml(klass, getParser(new InputSource(xml)));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T fromXml(Class<T> klass, String xml) {
        try {
            return fromXml(klass, getParser(InputSource.getInputSource(xml.getBytes())));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T parseAttributes(XmlPullParser parser, Object o, Class<T> klass) throws XmlPullParserException {
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            try {
                Field field = validateField(klass, name);
                if (field == null)
                    continue;
                field.set(o, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (T) o;
    }

    @Nullable
    private static <T> Field validateField(Class<T> klass, String name) {
        Field field = null;
        try {
            field = klass.getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (isFilter(field)) {
                return null;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    private <T> T fromXml(Class<T> klass, XmlPullParser parser) throws XmlPullParserException, IOException, InstantiationException, IllegalAccessException {
        T instance;
        instance = klass.newInstance();
        parseAttributes(parser, instance, klass);
        rParse(parser, instance, klass);
        return instance;
    }

    private static boolean systemType(Field field) {
        Class<?> klass = field.getType();
        return systemType(klass);
    }

    private static boolean systemType(Class<?> klass) {
        String className = klass.getName();
        for (String prefix : FILTER_PACKAGE) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFilter(Field field) {
        int flag = field.getModifiers();
        if ((flag & Modifier.STATIC) != 0) {
            return true;
        }
        return false;
    }

    private static Object createMap(XmlPullParser parser, Class<?> klass) {
        int depth = parser.getDepth();
        int type;
        Map o = null;
        try {
            o = (Map) klass.newInstance();
            while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth) &&
                    type != XmlPullParser.END_DOCUMENT) {
                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                final String key = parser.getAttributeValue(0);
                final String value = parser.nextText();
                try {
                    o.put(key, value);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private static Object createList(XmlPullParser parser, Class<?> rawType, Class<?> genericType) {
        int depth = parser.getDepth();
        int type;
        Collection o = null;
        try {
            o = (Collection) rawType.newInstance();
            while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth) &&
                    type != XmlPullParser.END_DOCUMENT) {
                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                o.add(createObject(parser, genericType));
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private static Object createObject(XmlPullParser parser, Class<?> klass) {
        final int depth = parser.getDepth();
        int type;
        Object o = null;
        try {
            o = klass.newInstance();
            parseAttributes(parser, o, klass);
            while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth) &&
                    type != XmlPullParser.END_DOCUMENT) {
                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                final String tag = parser.getName();
                try {
                    Field field = klass.getDeclaredField(tag);
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    setFieldValue(parser, o, field);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    private static void setFieldValue(XmlPullParser parser, Object o, Field field) throws IllegalAccessException, XmlPullParserException, IOException {
        Class[] classes = getFieldType(field);
        if (classes != null && classes.length == 2) {
            field.set(o, createList(parser, classes[0], classes[1]));
        } else if (classes != null && classes.length == 1) {
            field.set(o, createObject(parser, classes[0]));
        } else if (classes != null && classes.length == 3) {
            field.set(o, createMap(parser, classes[0]));
        } else {
            field.set(o, parser.nextText());
        }
    }

    private static Class[] getFieldType(Field field) {
        Class[] klass = null;
        Class<?> fieldType = field.getType();
        if (isList(fieldType)) {
            klass = new Class[2];
            klass[0] = fieldType;
            Type t = field.getGenericType();
            if (t instanceof ParameterizedType) {
                klass[1] = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
            }
        } else if (isMap(fieldType)) {
            klass = new Class[3];
            klass[0] = fieldType;
        } else if (!systemType(field)) {
            klass = new Class[1];
            klass[0] = fieldType;
        }
        return klass;
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            Log.e(TAG, "destroy xml parse.");
        } finally {
            super.finalize();
        }
    }

    private static void rParse(XmlPullParser parser, Object o, Class<?> klass) {
        int depth = parser.getDepth();
        int type;
        try {
            while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                final String name = parser.getName();
                Log.e(TAG, name);
                try {
                    Field field = validateField(klass, name);
                    if (field == null) {
                        continue;
                    }
                    setFieldValue(parser, o, field);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toXml(Object o) {
        return toXml(o, o.getClass());
    }

    public String toXml(Object o, Type type) {
        Document document = createDocument(o);
        TypeToken typeToken = TypeToken.getTypeToken(type);
        Class<?> rawType = typeToken.getRawType();
        Class<?> clz = typeToken.getActualType();
        document.appendChild(rXml(document, rawType.getSimpleName().toLowerCase(), o, rawType, clz));
        return generateXml(document);
    }

    private String generateXml(Document document) {
        try {
            Properties properties = new Properties();
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
            properties.setProperty(OutputKeys.VERSION, "1.0");
            properties.setProperty(OutputKeys.ENCODING, "utf-8");
            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            properties.setProperty(OutputKeys.STANDALONE, "yes");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperties(properties);

            DOMSource domSource = new DOMSource(document.getDocumentElement());
            OutputStream output = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(domSource, result);
            return output.toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    private Node rXml(Document document, String name, Object o, Class<?> klass, Class<?> actualType) {
        Node node;
        if (isList(klass)) {
            node = createListNode(document, name, o, klass, actualType);
        } else if (isMap(klass)) {
            node = createMapNode(document, name, o, klass);
        } else if (!systemType(klass)) {
            node = createObjectNode(document, name, o, klass);
        } else {
            node = createBaseNode(document, name, o, klass);
        }
        return node;
    }


    private Document createDocument(Object o) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            return document;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Node createObjectNode(Document document, String name, Object o, Class<?> klass) {
        Field[] fields = klass.getDeclaredFields();
        Element element = document.createElement(name);
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (isFilter(field)) {
                    continue;
                }
                Class[] classes = getFieldType(field);
                if (classes != null && classes.length == 2) {
                    Node node = rXml(document, field.getName(), field.get(o), classes[0], classes[1]);
                    if (node != null) {
                        element.appendChild(node);
                    }
                } else if (classes != null && classes.length == 3) {
                    Node node = rXml(document, field.getName(), field.get(o), classes[0], classes[0]);
                    if (node != null) {
                        element.appendChild(node);
                    }
                } else if (classes != null && classes.length == 1) {
                    Node node = rXml(document, field.getName(), field.get(o), classes[0], classes[0]);
                    if (node != null) {
                        element.appendChild(node);
                    }
                } else {
                    Class<?> type = field.getType();
                    Node node = rXml(document, field.getName(), field.get(o), type, type);
                    if (node != null) {
                        element.appendChild(node);
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return element;
    }

    private Node createListNode(Document document, String name, Object o, Class<?> klass, Class<?> actualType) {
        Collection collection = (Collection) o;
        if (collection == null) {
            return null;
        }
        Element element = document.createElement(name);
        Object[] array = collection.toArray();
        final int size = array.length;
        for (int i = 0; i < size; i++) {
            Object object = array[i];
            element.appendChild(rXml(document, actualType.getSimpleName().toLowerCase(), object, actualType, actualType));
        }
        return element;
    }

    private Node createMapNode(Document document, String name, Object o, Class<?> klass) {
        Map map = (Map) o;
        if (map == null) {
            return null;
        }
        Element element = document.createElement(name);
        Set keys = map.keySet();
        for (Object key : keys) {
            Element keyNode = document.createElement(KEY);
            keyNode.setAttribute(NAME, key.toString());
            Node keyNodeValue = document.createTextNode(map.get(key).toString());
            keyNode.appendChild(keyNodeValue);
            element.appendChild(keyNode);
        }
        return element;
    }

    private Node createBaseNode(Document document, String name, Object o, Class<?> klass) {
        Element element = document.createElement(name);
        Node node = document.createTextNode(o.toString());
        element.appendChild(node);
        return element;
    }
}
