package com.restfriedchicken.android.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfriedchicken.android.R;
import com.restfriedchicken.android.RestfriedChickenApp;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class DisplayMyOrdersActivity extends Activity {
    private ListView myOrdersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_my_orders);
        myOrdersView = (ListView) findViewById(android.R.id.list);
        myOrdersView.setEmptyView(findViewById(android.R.id.empty));
        myOrdersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetMyOrdersTask(this).execute();
    }

    private class GetMyOrdersTask extends AsyncTask<Void, Void, MyOrdersRepresentation> {

        private Activity activity;

        private GetMyOrdersTask(Activity context) {
            this.activity = context;
        }

        @Override
        protected MyOrdersRepresentation doInBackground(Void... params) {
            try {
                final String url = customerServiceBaseUrl() + "/1/orders";
                MyOrdersRepresentation orders = getRestTemplate(jsonMessageConverter(objectMapper())).getForObject(url, MyOrdersRepresentation.class);
                return orders;
            } catch (Exception e) {
                Log.e("DisplayMyOrdersActivity", e.getMessage(), e);
            }
            return new MyOrdersRepresentation();
        }

        private String customerServiceBaseUrl() {
            RestfriedChickenApp application = (RestfriedChickenApp) activity.getApplication();
            return application.customerServiceBaseUrl();
        }

        @Override
        protected void onPostExecute(MyOrdersRepresentation orders) {

            List<MyOrderRepresentation> orderList = orders.getOrders();

            MyOrderRepresentation[] orderArray = new MyOrderRepresentation[orderList.size()];

            for (int i = 0; i < orderList.size(); i++) {
                orderArray[i] = orderList.get(i);
            }

            myOrdersView.setAdapter(new MyOrdersAdapter(activity,
                    android.R.layout.simple_list_item_1, orderArray, myOrdersView));
        }

        private RestTemplate getRestTemplate(MappingJackson2HttpMessageConverter converter) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(converter);
            return restTemplate;
        }

        private MappingJackson2HttpMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setObjectMapper(objectMapper);
            return converter;
        }

        private ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            return objectMapper;
        }

    }
}
