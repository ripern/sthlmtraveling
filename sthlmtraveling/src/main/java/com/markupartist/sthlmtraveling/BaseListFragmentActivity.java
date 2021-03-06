package com.markupartist.sthlmtraveling;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAgent;

public class BaseListFragmentActivity extends SherlockFragmentActivity implements OnItemClickListener {

    private ListView mListView;

	@Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, MyApplication.ANALYTICS_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onPageView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
     }

    protected void registerEvent(String event) {
        FlurryAgent.onEvent(event);
    }

    protected void registerEvent(String event, Map<String, String> parameters) {
        FlurryAgent.onEvent(event, parameters);
    }

    protected ActionBar initActionBar(int menuResource) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowHomeEnabled(true);
        return actionBar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            final Intent startIntent = new Intent(this, StartActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Sets the list view to the default resource value android.R.id.list. Call this after setContentView
     */
    protected void setDefaultListView()
    {
    	mListView =(ListView) findViewById(android.R.id.list);
    	mListView.setOnItemClickListener(this);
    }

	public ListView getListView() {
		return mListView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Implement in subclass
	}
}
