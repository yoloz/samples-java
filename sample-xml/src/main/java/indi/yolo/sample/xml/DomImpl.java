package indi.yolo.sample.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * @author yoloz
 */
public class DomImpl {

    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private Node addLibrary(Document document) {
        Element library = document.createElement("library");
        library.setAttribute("type", "jar");
        library.setAttribute("path", "${home}\\AppData\\Roaming\\DBeaverData\\drivers\\zhds");
        library.setAttribute("custom", "true");
        return library;
    }

    private Node addDriver(Document document) {
        Element library = document.createElement("driver");
        library.setAttribute("id", "zhds");
        library.setAttribute("categories", "sql");
        library.setAttribute("name", "ZHDS");
        library.setAttribute("class", "com.zhds.jdbc.Driver");
        library.setAttribute("url", "jdbc:zhds://{host}:{port}/{database}");
        library.setAttribute("port", "10000");
        library.setAttribute("description", "Driver for ZHDS");
        library.setAttribute("custom", "false");
        library.appendChild(addLibrary(document));

        Element supports_indexes = document.createElement("parameter");
        supports_indexes.setAttribute("name", "supports-indexes");
        supports_indexes.setAttribute("value", "true");
        library.appendChild(supports_indexes);

        Element all_objects_pattern = document.createElement("parameter");
        all_objects_pattern.setAttribute("name", "all-objects-pattern");
        all_objects_pattern.setAttribute("value", "%");
        library.appendChild(all_objects_pattern);

        Element quote_reserved_words = document.createElement("parameter");
        quote_reserved_words.setAttribute("name", "quote-reserved-words");
        quote_reserved_words.setAttribute("value", "true");
        library.appendChild(quote_reserved_words);

        Element omit_schema = document.createElement("parameter");
        omit_schema.setAttribute("name", "omit-schema");
        omit_schema.setAttribute("value", "false");
        library.appendChild(omit_schema);

        Element schema_filters_enabled = document.createElement("parameter");
        schema_filters_enabled.setAttribute("name", "schema-filters-enabled");
        schema_filters_enabled.setAttribute("value", "false");
        library.appendChild(schema_filters_enabled);

        Element script_delimiter_redefiner = document.createElement("parameter");
        script_delimiter_redefiner.setAttribute("name", "script-delimiter-redefiner");
        script_delimiter_redefiner.setAttribute("value", ";");
        library.appendChild(script_delimiter_redefiner);

        Element supports_struct_cache = document.createElement("parameter");
        supports_struct_cache.setAttribute("name", "supports-struct-cache");
        supports_struct_cache.setAttribute("value", "true");
        library.appendChild(supports_struct_cache);

        Element supports_truncate = document.createElement("parameter");
        supports_truncate.setAttribute("name", "supports-truncate");
        supports_truncate.setAttribute("value", "true");
        library.appendChild(supports_truncate);

        Element omit_type_cache = document.createElement("parameter");
        omit_type_cache.setAttribute("name", "omit-type-cache");
        omit_type_cache.setAttribute("value", "false");
        library.appendChild(omit_type_cache);

        Element split_procedures_and_functions = document.createElement("parameter");
        split_procedures_and_functions.setAttribute("name", "split-procedures-and-functions");
        split_procedures_and_functions.setAttribute("value", "false");
        library.appendChild(split_procedures_and_functions);

        Element supports_stored_code = document.createElement("parameter");
        supports_stored_code.setAttribute("name", "supports-stored-code");
        supports_stored_code.setAttribute("value", "true");
        library.appendChild(supports_stored_code);

        Element supports_references = document.createElement("parameter");
        supports_references.setAttribute("name", "supports-references");
        supports_references.setAttribute("value", "true");
        library.appendChild(supports_references);

        Element omit_single_catalog = document.createElement("parameter");
        omit_single_catalog.setAttribute("name", "omit-single-catalog");
        omit_single_catalog.setAttribute("value", "false");
        library.appendChild(omit_single_catalog);

        Element ddl_drop_column_brackets = document.createElement("parameter");
        ddl_drop_column_brackets.setAttribute("name", "ddl-drop-column-brackets");
        ddl_drop_column_brackets.setAttribute("value", "false");
        library.appendChild(ddl_drop_column_brackets);

        Element omit_single_schema = document.createElement("parameter");
        omit_single_schema.setAttribute("name", "omit-single-schema");
        omit_single_schema.setAttribute("value", "false");
        library.appendChild(omit_single_schema);

        Element supports_scroll = document.createElement("parameter");
        supports_scroll.setAttribute("name", "supports-scroll");
        supports_scroll.setAttribute("value", "false");
        library.appendChild(supports_scroll);

        Element omit_catalog = document.createElement("parameter");
        omit_catalog.setAttribute("name", "omit-catalog");
        omit_catalog.setAttribute("value", "false");
        library.appendChild(omit_catalog);

        Element supports_views = document.createElement("parameter");
        supports_views.setAttribute("name", "supports-views");
        supports_views.setAttribute("value", "true");
        library.appendChild(supports_views);

        Element script_delimiter = document.createElement("parameter");
        script_delimiter.setAttribute("name", "script-delimiter");
        script_delimiter.setAttribute("value", ";");
        library.appendChild(script_delimiter);

        Element legacy_sql_dialect = document.createElement("parameter");
        legacy_sql_dialect.setAttribute("name", "legacy-sql-dialect");
        legacy_sql_dialect.setAttribute("value", "false");
        library.appendChild(legacy_sql_dialect);

        Element script_delimiter_after_query = document.createElement("parameter");
        script_delimiter_after_query.setAttribute("name", "script-delimiter-after-query");
        script_delimiter_after_query.setAttribute("value", "false");
        library.appendChild(script_delimiter_after_query);

        Element use_search_string_escape = document.createElement("parameter");
        use_search_string_escape.setAttribute("name", "use-search-string-escape");
        use_search_string_escape.setAttribute("value", "false");
        library.appendChild(use_search_string_escape);

        Element supports_multiple_results = document.createElement("parameter");
        supports_multiple_results.setAttribute("name", "supports-multiple-results");
        supports_multiple_results.setAttribute("value", "false");
        library.appendChild(supports_multiple_results);

        Element script_delimiter_after_block = document.createElement("parameter");
        script_delimiter_after_block.setAttribute("name", "script-delimiter-after-block");
        script_delimiter_after_block.setAttribute("value", "false");
        library.appendChild(script_delimiter_after_block);


        Element ddl_drop_column_short = document.createElement("parameter");
        ddl_drop_column_short.setAttribute("name", "ddl-drop-column-short");
        ddl_drop_column_short.setAttribute("value", "false");
        library.appendChild(ddl_drop_column_short);

        Element supports_limits = document.createElement("parameter");
        supports_limits.setAttribute("name", "supports-limits");
        supports_limits.setAttribute("value", "true");
        library.appendChild(supports_limits);

        Element supports_select_count = document.createElement("parameter");
        supports_select_count.setAttribute("name", "supports-select-count");
        supports_select_count.setAttribute("value", "true");
        library.appendChild(supports_select_count);

        return library;
    }


    private void readXml() {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("sample-xml/src/main/resources/drivers.xml");
            Node driver = document.getElementsByTagName("drivers").item(0);
            NodeList providerList = driver.getChildNodes();
            out:
            for (int i = 0; i < providerList.getLength(); i++) {
                Node provider = providerList.item(i);
                if (provider.getNodeType() != Node.ELEMENT_NODE) continue;
                NamedNodeMap namedNodeMap = provider.getAttributes();
                for (int j = 0; j < namedNodeMap.getLength(); j++) {
                    Node attr = namedNodeMap.item(j);
                    if ("id".equals(attr.getNodeName()) && "zhds".equals(attr.getNodeValue())) {
                        NodeList driverList = provider.getChildNodes();
                        int counter = 0;
                        for (int k = 0; k < driverList.getLength(); k++) {
                            if (driverList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                counter++;
                            }
                            if (counter > 0) {
                                break;
                            }
                        }
                        System.out.println("*************" + counter);
                        if (counter == 0) {
                            System.out.println("********add zhds driver*************");
                            provider.appendChild(addDriver(document));
                        }
                        break out;
                    }
                }
            }
            writeXml(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeXml(Document document) {
        try {
            if (document == null) {
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.newDocument();
                Element provider = document.createElement("provider");
                provider.setAttribute("id", "zhds");
                document.appendChild(provider);
                provider.appendChild(addDriver(document));
            }
            document.setXmlStandalone(true);//xml文件的standalone为yes且不显示
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");//生成的XML文件标签会换行
            tf.transform(new DOMSource(document), new StreamResult(new File("sample-xml/src/main/resources/drivers_bak.xml")));
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        DomImpl domImpl = new DomImpl();
        domImpl.readXml();
//        domImpl.writeXml(null);
    }

    /**
     * org.w3c.dom.DOMException: WRONG_DOCUMENT_ERR: A node is used in a different document than the one that created it.
     * 读取模板插进去不可行，不同的document
     */
    private Node readTemplate() {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("sample-xml/src/main/resources/zhds.xml");
            NodeList driverList = document.getElementsByTagName("driver");
            for (int i = 0; i < driverList.getLength(); i++) {
                Node driver = driverList.item(i);
                driver.appendChild(addLibrary(document));
            }
            return document.getElementsByTagName("driver").item(0);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
