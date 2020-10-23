import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
	    /* ****************************************************************** */

        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.coffees,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //     DownloadServerData("http://cpscserv.dom.edu/faculty/mpolk/starbucks.aspx?flavor=Toffee%20Nut%20Cream");
    }


    public void lookupcals (View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        String flavorSelected = spinner.getSelectedItem().toString();
        int wherespace = flavorSelected.indexOf(' ');
        String part1, part2;
        while (wherespace != -1) {
            // still spaces to replace
            part1 = flavorSelected.substring(0,wherespace);
            part2 = flavorSelected.substring(wherespace+1);
            flavorSelected = part1 + "%20" + part2;
            wherespace = flavorSelected.indexOf(' ');
        }

        String url = "http://cpscserv.dom.edu/faculty/mpolk/starbucks.aspx?flavor=" + flavorSelected;
        //  	DownloadServerData("http://cpscserv.dom.edu/faculty/mpolk/starbucks.aspx?flavor=Toffee%20Nut%20Cream");
        DownloadServerData(url);

    }

    private InputStream OpenHttpConnection(String urlString)
            throws IOException
    {

        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);

        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            //   httpConn.setInstanceFollowRedirects(true);
            httpConn.setChunkedStreamingMode(0);
            httpConn.setRequestMethod("GET");

            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");
        }
        return in;
    }

    private void DownloadServerData(String URL)
    {
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);

            Document doc = null;
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db;

            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            doc.getDocumentElement().normalize();

            //---retrieve all the <calorieinfo> nodes---
            NodeList itemNodes = doc.getElementsByTagName("calorieinfo");

            String strTitle = "";
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node itemNode = itemNodes.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    //---convert the Node into an Element---
                    Element itemElement = (Element) itemNode;

                    //---get all the <calories> element under the <calorieinfo>
                    // element---
                    NodeList titleNodes =
                            (itemElement).getElementsByTagName("calories");

                    //---convert a Node into an Element---
                    Element titleElement = (Element) titleNodes.item(0);

                    //---get all the child nodes under the <title> element---
                    NodeList textNodes =
                            ((Node) titleElement).getChildNodes();

                    //---retrieve the text of the <calories> element---
                    strTitle = ((Node) textNodes.item(0)).getNodeValue();

                    TextView txt = (TextView) findViewById(R.id.textViewInfo);
                    txt.setText("Number of calories is: " + strTitle);
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}
