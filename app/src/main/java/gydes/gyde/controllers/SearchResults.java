package gydes.gyde.controllers;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;

import gydes.gyde.R;
import gydes.gyde.models.TourDetailsDialogFragment;
import gydes.gyde.models.Tour;
import gydes.gyde.models.TourListAdapter;

public class SearchResults extends AppCompatActivity {
    Context context = this;
    SearchView searchView;

//    final ArrayList<Tour> tours;

    final ArrayList<Tour> tours = new ArrayList<Tour>();
//    ButtonAdapter adapter;

    public static final int SEARCH_RESULTS_BUTTON_OPT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = findViewById(R.id.filter_toolbar);
        setSupportActionBar(toolbar);

        // Setup navigation drawer, action bar and status bar
        final Drawer result = NavigationDrawerBuilder.build(this, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.gydeYellow));

        searchView = (SearchView) findViewById(R.id.filter_search_bar);
        searchView.setFocusable(false);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchBar = (SearchView)findViewById(R.id.filter_search_bar);
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResults.class)));
        searchBar.setFocusable(true);
        searchBar.setIconifiedByDefault(true);
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = SearchResults.toCamelCase(intent.getStringExtra(SearchManager.QUERY).trim());
//            final ArrayList<Tour> tours = new ArrayList<Tour>();
//            tours = new ArrayList<Tour>();

            /*
<<<<<<< HEAD
            final ButtonAdapter adapter = new ButtonAdapter(this, R.layout.tour_list_item, tours);
//            adapter = new ButtonAdapter(this, R.layout.tour_list_item, tours);

=======*/
            final TourListAdapter adapter = new TourListAdapter(this, R.layout.tour_list_item, tours, SEARCH_RESULTS_BUTTON_OPT);
//>>>>>>> master
            ListView listView = findViewById(android.R.id.list);
            listView.setAdapter(adapter);

            final DatabaseReference toursRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_tours_path));
            toursRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(query)) {
                        for (DataSnapshot child : dataSnapshot.child(query).getChildren()) {
                            tours.add(child.getValue(Tour.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("data access", "Error querying tours: " + databaseError.getCode());
                }
            });
        }
    }

    public static String toCamelCase(String str) {
        String[] words = str.split(" ");
        String camelCaseStr = "";
        for(int i = 0; i < words.length; i++) {
            camelCaseStr += words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();
            if(i < words.length - 1) {
                camelCaseStr += " ";
            }
        }
        return camelCaseStr;
    }

    class ButtonAdapter extends ArrayAdapter<Tour> {
        private Context context;
        private int resourceID;
        private ArrayList<Tour> tourList;

        public ButtonAdapter(@NonNull Context c, int rID, ArrayList<Tour> list) {
            super(c, 0, list);
            context = c;
            resourceID = rID;
            tourList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(context).inflate(resourceID, parent, false);
            }

            final Tour currTour = tourList.get(position);

            TextView name = listItem.findViewById(R.id.textView_name);
            name.setText(currTour.getName());

            TextView stops = listItem.findViewById(R.id.textView_stops);
            stops.setText(currTour.getStops());

            TextView tags = listItem.findViewById(R.id.textView_tags);
            tags.setText(currTour.getTags());

            Button viewButton = listItem.findViewById(R.id.view_button);
            viewButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TourDetailsDialogFragment frag = TourDetailsDialogFragment.newInstance(currTour, SEARCH_RESULTS_BUTTON_OPT);
                    frag.show(getFragmentManager(), "tour details");
                }
            });

            return listItem;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.filter:
                Toast.makeText(this, "filter clicked", Toast.LENGTH_SHORT).show();
                openDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void openDialog() {
        /*
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.filter_dialog);

        dialog.show();*/

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchResults.this);
        View view = getLayoutInflater().inflate(R.layout.tour_filter_layout_dialog, null);
        builder.setView(view)
                .setTitle("Filter")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText durationBox = (EditText) view.findViewById(R.id.duration_box);
                        EditText tagsBox = (EditText) view.findViewById(R.id.tags_box);

                        final ArrayList<Tour> toursCopy = new ArrayList<>(tours);
                        final TourListAdapter adapter2 = new TourListAdapter(context, R.layout.tour_list_item, toursCopy, SEARCH_RESULTS_BUTTON_OPT);
                        ListView listView2 = findViewById(android.R.id.list);
                        listView2.setAdapter(adapter2);






                        String inputDurationText = durationBox.getText().toString();
                        String inputTagsText = tagsBox.getText().toString();

                        if(!inputDurationText.isEmpty()) {
                            int inputDuration = Integer.parseInt(inputDurationText);
                            toursCopy.removeIf((Tour tour) -> tour.getDuration() != inputDuration);

                        }

                        if(!inputTagsText.isEmpty()) {
                            String[] tags = inputTagsText.split(", ");

                            for(int i=0; i<tags.length; i++) {
                                String tag = tags[i];
                                toursCopy.removeIf((Tour tour) -> !tour.getTags().contains(tag));
                            }
                        }


                        adapter2.notifyDataSetChanged();

                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();

        /*

        Button filterButton = (Button) view.findViewById(R.id.filter_button);
        EditText durationBox = (EditText) view.findViewById(R.id.duration_box);

        final ArrayList<Tour> toursCopy = new ArrayList<>(tours);
        final ButtonAdapter adapter2 = new ButtonAdapter(this, R.layout.tour_list_item, toursCopy);
        ListView listView2 = findViewById(android.R.id.list);
        listView2.setAdapter(adapter2);

        filterButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String inputDurationText = durationBox.getText().toString();

                        if(!inputDurationText.isEmpty()) {
                            int inputDuration = Integer.parseInt(inputDurationText);

                            toursCopy.removeIf((Tour tour) -> tour.getDuration() != inputDuration);
                        }
                        adapter2.notifyDataSetChanged();
                    }
                }
        );*/

    }
}