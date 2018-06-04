package gydes.gyde.controllers.addTour;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import org.w3c.dom.Text;

import gydes.gyde.R;
import gydes.gyde.controllers.KeyboardManager;
import gydes.gyde.controllers.NavigationDrawerBuilder;

/**
 * Created by kelvinlui1 on 5/30/18.
 */

public class AddTourConfirmName extends AppCompatActivity {
    private final int WORD_COUNT = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour_confirm_name);

        // Open keyboard as soon as view is loaded
        KeyboardManager.showKeyboard(this);

        // Setup navigation drawer, action bar and status bar
        final Drawer result = NavigationDrawerBuilder.build(this, savedInstanceState);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        // Setup wordcount
        final TextView wordCountView = (TextView) findViewById(R.id.word_count);
        wordCountView.setText("(" + WORD_COUNT + " characters left)");

        // Setup listener to edit text
        final EditText editText = (EditText) findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                wordCountView.setText("(" + (WORD_COUNT - i) + " characters left)");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Setup continue button
        final FloatingActionButton continueButton = (FloatingActionButton) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().length() == 0) {
                    Toast.makeText(getBaseContext(), "Can not leave tour name empty. Please try again.", Toast.LENGTH_LONG).show();;
                    return;
                }

                Intent intent = new Intent(getBaseContext(), AddTourConfirmCapacity.class);
                Bundle bundle = getIntent().getExtras();
                bundle.putString("name", editText.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
