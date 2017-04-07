package com.twistedplane.sealnote;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.twistedplane.sealnote.data.DatabaseHandler;
import com.twistedplane.sealnote.data.Note;
import com.twistedplane.sealnote.fragment.SealnoteFragment;
import com.twistedplane.sealnote.utils.FontCache;
import com.twistedplane.sealnote.utils.Misc;
import com.twistedplane.sealnote.utils.PreferenceHandler;
import com.twistedplane.sealnote.utils.TimeoutHandler;
import com.twistedplane.sealnote.view.simplelist.SimpleListFragment;
import com.twistedplane.sealnote.view.staggeredgrid.StaggeredGridFragment;
import org.codechimp.apprater.AppRater;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;


/**
 * Main activity where all cards are listed in a staggered grid
 */
public class SealnoteActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String TAG = "SealnoteActivity";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerTagListView;

    private Note.Folder mCurrentFolder;
    private Map<String, Integer> mTags;
    private int mTagId;
    private String mTagName;

    private SealnoteFragment mSealnoteFragment;
    private boolean mReloadFragment = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Creating main activity...");

        if (SealnoteApplication.getDatabase().getPassword() == null) {
            // onResume will follow up which will start PasswordActivity and setup database password
            Log.d(TAG, "Password can't be found while creating activity. Start PasswordActivity");
            return;
        }

        /* Handle saved state */
        if (savedInstanceState != null) {
            String folder = savedInstanceState.getString("SN_FOLDER", Note.Folder.FOLDER_LIVE.name());
            mCurrentFolder = Note.Folder.valueOf(folder);
            mTagId = savedInstanceState.getInt("SN_TAGID", -1);
            // This only makes sense when mTagId != -1
            mTagName = savedInstanceState.getString("SN_TAGNAME", null);
        } else {
            mCurrentFolder = Note.Folder.FOLDER_LIVE;
            mTagId = -1;
        }

        /* Load and initialize views */
        setContentView(R.layout.main);
        Misc.secureWindow(this);

        loadNotesView();
        initNavigationDrawer();

        /* Shared Preferences */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        /* Setup ActionBar */
        ActionBar actionBar = getActionBar();
        if (actionBar == null)
            return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        if (mCurrentFolder != Note.Folder.FOLDER_TAG) {
            //FIXME: Cleanup
            actionBar.setTitle(getResources().getStringArray(
                    R.array.navigation_drawer
            )[mCurrentFolder.ordinal() - 1]);
        } else {
            actionBar.setTitle(mTagName);
        }

        AppRater.app_launched(this);
    }

    private void loadNotesView() {
        final PreferenceHandler.NoteListViewType type = PreferenceHandler.getNoteListViewType(this);

        switch (type) {
            case VIEW_TILES:
                mSealnoteFragment = new StaggeredGridFragment();
                break;
            case VIEW_COLUMN:
                mSealnoteFragment = new StaggeredGridFragment();
                break;
            case VIEW_SIMPLE_LIST:
                mSealnoteFragment = new SimpleListFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("SN_FOLDER", mCurrentFolder.name());
        bundle.putInt("SN_TAGID", mTagId);
        mSealnoteFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mSealnoteFragment);
        transaction.commit();
    }

    private Map<String, Integer> getTags() {
        return mTags;
    }

    /**
     * Set up drawer layout and listeners
     */
    private void initNavigationDrawer() {
        final View drawerContent = findViewById(R.id.drawer_content);
        final ListView  folderList = (ListView) findViewById(R.id.drawer_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerTagListView = (ListView) findViewById(R.id.drawer_tags);

        /* Setup drawer */

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. **/
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (mReloadFragment) {
                    mSealnoteFragment.setFolder(mCurrentFolder,  mTagId);
                    getActionBar().setTitle(mTagName);
                    mReloadFragment = false;
                }
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. **/
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /* Setup Drawer List for Folders */

        String[]        itemStrings = getResources().getStringArray(R.array.navigation_drawer);
        TypedArray      itemIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);

        folderList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        folderList.setAdapter(new NavigationDrawerAdapter(
                this,
                R.layout.drawer_list_item,
                itemStrings,
                itemIcons
        ));

        folderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //FIXME
                final Note.Folder newFolder;
                switch (pos) {
                    case 0:
                        newFolder = Note.Folder.FOLDER_LIVE;
                        break;
                    case 1:
                        newFolder = Note.Folder.FOLDER_ARCHIVE;
                        break;
                    case 2:
                        newFolder = Note.Folder.FOLDER_TRASH;
                        break;
                    default:
                        newFolder = Note.Folder.FOLDER_LIVE;
                }

                mReloadFragment = (newFolder != mCurrentFolder);
                mCurrentFolder = newFolder;
                mTagId = -1;

                mDrawerTagListView.clearChoices();
                ((ArrayAdapter) mDrawerTagListView.getAdapter()).notifyDataSetChanged();
                folderList.setItemChecked(pos, true);

                mTagName = getResources().getStringArray(R.array.navigation_drawer)[pos];
                mDrawerLayout.closeDrawer(drawerContent);
            }
        });

        /* On first start we need to select the first item on drawer */
        switch (mCurrentFolder) {
            case FOLDER_LIVE:
                folderList.setItemChecked(0, true);
                break;
            case FOLDER_ARCHIVE:
                folderList.setItemChecked(1, true);
                break;
            case FOLDER_TRASH:
                folderList.setItemChecked(1, true);
                break;
        }

        /* Setup Drawer List for Tags */
        mDrawerTagListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mDrawerTagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                mTagName = (String) adapterView.getItemAtPosition(pos);

                int tagid = mTags.get(mTagName);
                mReloadFragment = (tagid != mTagId);
                mTagId = tagid;
                mCurrentFolder = Note.Folder.FOLDER_TAG;

                folderList.clearChoices();
                mDrawerTagListView.setItemChecked(pos, true);

                mDrawerLayout.closeDrawer(drawerContent);
            }
        });
    }

    private void reloadTagsAdapter() {
        DatabaseHandler handler = SealnoteApplication.getDatabase();
        mTags = handler.getAllTags();
        Set<String> tagSet = mTags.keySet();
        String []tags = mTags.keySet().toArray(new String[tagSet.size()]);

        mDrawerTagListView.setAdapter(
                new ArrayAdapter<String>(
                        this, R.layout.drawer_tag_list_item, tags
                )
        );

        /* Select currently active tag if any in list view */
        if (mTagId != -1) {
            // We need to do this every resume/recreate but not
            // for Folder list, because folder list is static while
            // tags list's adapter is dynamic and changed
            int position = Arrays.asList(tags).indexOf(mTagName);
            mDrawerTagListView.setItemChecked(position, true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("NoteListViewType") || key.equals("DynamicFontSize")) {
            Log.d(TAG, "NoteListView type changed!");
            mReloadFragment = true;
        }
    }

    /**
     * When coming back from foreground check if timeout has expired and if
     * so load logout to password activity. Otherwise reset the timeout status.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (TimeoutHandler.instance().resume(this)) {
            Log.d(TAG, "Timed out! Going to password activity");
            return;
        }

        if (mReloadFragment) {
            loadNotesView();
            mReloadFragment = false;
        }

        reloadTagsAdapter();

        // preference may have changed, so do it again. Happens when coming
        // back from settings activity
        Misc.secureWindow(this);
    }

    /**
     * Schedules a delayed callback after configured password expiry timeout.
     */
    @Override
    public void onPause() {
        super.onPause();
        TimeoutHandler.instance().pause(this);
    }

    /**
     * Save state for activity recreation
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current folder/tags in use
        outState.putString("SN_FOLDER", mCurrentFolder.name());
        outState.putInt("SN_TAGID", mTagId);

        // Since we are going to use TAGNAME only when mTagId != -1, and in
        // that case the title is the best and fastest way to get tagname
        outState.putString("SN_TAGNAME", getActionBar().getTitle().toString());
    }

    /**
     * Inflate the ActionBar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Update actionbar when invalidated
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        MenuItem addMenuItem = menu.findItem(R.id.action_new_note);
        addMenuItem.setVisible(mCurrentFolder == Note.Folder.FOLDER_LIVE);
        return result;
    }

    /**
     * An item is selected from ActionBar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                return true;
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_logout:
                TimeoutHandler.instance().expire(this);
                return true;
            case R.id.action_new_note_generic:
                onCreateNoteClick(null);
                return true;
            case R.id.action_new_note_card:
                NoteActivity.startForNoteId(SealnoteActivity.this, -1, Note.Type.TYPE_CARD);
                return true;
            case R.id.action_new_note_login:
                NoteActivity.startForNoteId(SealnoteActivity.this, -1, Note.Type.TYPE_LOGIN);
                return true;
            case R.id.action_edit_tags:
                showEditTagsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create and show about dialog
     */
    private void showAboutDialog() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    /**
     * Start settings activity
     */
    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    /**
     * Dialog to edit/delete tags
     */
    private void showEditTagsDialog() {
        Intent intent = new Intent(this, TagsEditorActivity.class);
        this.startActivity(intent);
    }

    /**
     * Called when any create new button/action is clicked.
     */
    public void onCreateNoteClick(View view) {
        NoteActivity.startForNoteId(SealnoteActivity.this, -1, null);
    }

    /**
     * Adapter to show list of folders in drawer
     */
    private class NavigationDrawerAdapter extends ArrayAdapter<String> {
        private LayoutInflater mInflater;
        private String[] mStrings;
        private TypedArray mIcons;
        private int mViewResourceId;

        public NavigationDrawerAdapter(Context ctx, int viewResourceId, String[] strings, TypedArray icons) {
            super(ctx, viewResourceId, strings);
            mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mStrings = strings;
            mIcons = icons;
            mViewResourceId = viewResourceId;
        }

        @Override
        public int getCount() {
            return mStrings.length;
        }

        @Override
        public String getItem(int position) {
            return mStrings[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView != null) {
                view = convertView;
            } else {
                view = mInflater.inflate(mViewResourceId, null);
            }

            ImageView iv = (ImageView) view.findViewById(R.id.item_icon);
            iv.setImageDrawable(mIcons.getDrawable(position));

            TextView tv = (TextView) view.findViewById(R.id.text1);
            tv.setText(mStrings[position]);

            if (convertView == null) {
                tv.setTypeface(FontCache.getFont(getContext(), "RobotoCondensed-Regular"), Typeface.NORMAL);
            }

            return view;
        }
    }
}
