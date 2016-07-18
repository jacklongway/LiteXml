package com.longway.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<province name=\"湖南\">\n" +
                "    <cities>\n" +
                "        <city name=\"长沙\" code=\"1001\" />\n" +
                "        <city>\n" +
                "            <name>益阳</name>\n" +
                "            <code>1002</code>\n" +
                "            <regions>\n" +
                "                <region>\n" +
                "                    <name>资阳区</name>\n" +
                "                </region>\n" +
                "                <region>\n" +
                "                    <name>朝阳区</name>\n" +
                "                </region>\n" +
                "            </regions>\n" +
                "        </city>\n" +
                "    </cities>\n" +
                "    <map>\n" +
                "        <key name=\"aa\">bb</key>\n" +
                "        <key name=\"cc\">dd</key>\n" +
                "    </map>\n" +
                "    <code>1000</code>\n" +
                "    <introduce>\n" +
                "        <title>古城</title>\n" +
                "        <desc>古都</desc>\n" +
                "    </introduce>\n" +
                "</province>";

        Province province = new Xml().fromXml(new TypeToken<Province>() {
        }.getType(), xml);
        Log.e(TAG, province.toString());

        String pXml = new Xml().toXml(province);
        Log.e(TAG, pXml);

        String booksXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!-- list,set-->\n" +
                "<list>\n" +
                "    <book>\n" +
                "        <name>java</name>\n" +
                "        <author>goms</author>\n" +
                "    </book>\n" +
                "    <book>\n" +
                "        <name>android</name>\n" +
                "        <author>google</author>\n" +
                "    </book>\n" +
                "</list>";

        ArrayList<Book> books = new Xml().fromXml(new TypeToken<ArrayList<Book>>() {
        }.getType(), booksXml);
        Log.e("books", books.toString());

        String listXml = new Xml().toXml(books, new TypeToken<ArrayList<Book>>() {
        }.getType());
        Log.e(TAG, listXml);

        String mapXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!-- map -->\n" +
                "<map>\n" +
                "    <key name=\"aa\">bb</key>\n" +
                "    <key name=\"cc\">dd</key>\n" +
                "</map>";

        HashMap<String, String> map = new Xml().fromXml(new TypeToken<HashMap<String, String>>() {
        }.getType(), mapXml);
        Log.e(TAG, map.toString());

        String mXml = new Xml().toXml(map, new TypeToken<HashMap<String, String>>() {
        }.getType());
        Log.e(TAG, mXml);
    }
}
