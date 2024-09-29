package adet.basiccalculator.T137.LIGAYAO;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private String display="";
    private EditText input_text;
    private TextView display_text;
    private String currentOperator="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button delete_btn = (Button) findViewById(R.id.butdelete);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_last_char();
            }
        });
        


        TextView screen = (TextView) findViewById(R.id.input_box);
        screen.setText(display);
        input_text = findViewById(R.id.input_box);
        display_text = findViewById(R.id.result_box);
        if (savedInstanceState != null) {
            String currentText = savedInstanceState.getString("last_result");
            display_text.setText(currentText);
        }
        // TODO: bind clickEvent to magic btn
        Button magic = (Button) findViewById(R.id.butmagic);
        magic.setOnClickListener(v -> {
            Toast.makeText(this, "Congrats You Clicked a Useless Button.", Toast.LENGTH_SHORT).show();
        });

        // TODO: bind clickEvent for decimal button
        Button decimal_btn = (Button) findViewById(R.id.butdec);
        decimal_btn.setOnClickListener(v -> {

            // TODO:  handle cases where user adds decimal when expr is empty or the expr ends with OP
            if(getInput().isEmpty() ||  endsWithOperator()){
                appendToLast("0.");
                return;
            }

            // TODO: check the last value in the expr if it contains a decimal point already
            String[] nums = getInput().split("[\\+\\-x÷]");
            Log.d("DEEZ", "onCreate: "+Arrays.toString(nums));
            String last_num = nums[nums.length - 1];

            if(last_num.contains(".")){
                Log.d("dumbUser", "onCreate: dumb user trying to add . when a . already exists");
                return;
            }
            appendToLast(".");
        });

    }

    private void appendToLast(String str) {
        this.input_text.getText().append(str);
    }

    public void onClickNumber(View v) {
        Button b = (Button) v;
        display += b.getText();
        appendToLast(display);
        display="";
    }
    public void onToggleSign(View v){
        // TODO: don't do anything if the expr ends with operator
        if(endsWithOperator()){
            Log.d("DumbUser", "onToggleSign: we caught a dumb user trying to toggle sign when the expr ends with an operator");
            return;
        }

        // TODO: handle user trying to toggle sign even when the expr is empty
        if(getInput().isEmpty()){
            Log.d("DumbUser", "onToggleSign: we caught a dumb user trying to toggle sign when the expr is empty");
            return;
        }

        // TODO: we are going to do a stupid check, as we care about the end value of the expr so we gonna check is the expr ends with (-x) pattern
        Pattern pattern = Pattern.compile("\\(-\\d+(?:\\.\\d*)?\\)$");
        Matcher matcher = pattern.matcher(getInput());
        if (matcher.find()) {
            Log.d("test", "onToggleSign: not yet implemented, but we found (-x) at the end, early return");
            String matchedString = matcher.group();
            // INFO: this is magic xD
            replace(matcher.group(), matchedString.substring(2, matchedString.length() - 1));
            return;
        }
        // TODO: we are going to split the text by operators
        String[] nums = getInput().split("[\\+\\-x÷]");
        Log.d("test", "onToggleSign: " + Arrays.toString(nums) + "|" + getInput());
        // TODO: replace the last number with (-X)
        replace(nums[nums.length - 1],"(-"+nums[nums.length - 1]+")");
    }
    public void onClickOperator(View v) {
        Button b = (Button) v;
        display += b.getText();
        if (endsWithOperator())
        {
            replace(display);
            currentOperator = b.getText().toString();
            display = "";
        }
        else {
            appendToLast(display);
            currentOperator = b.getText().toString();
            display = "";
        }

    }

    public void onClearButton(View v) {
        input_text.getText().clear();
        display_text.setText("");
    }

    public void delete_last_char() {
        try {
            this.input_text.getText().delete(getInput().length() - 1, getInput().length());
        } catch (IndexOutOfBoundsException e){
            Log.d("deez", "deletenumber: caught a dumb user spamming delete");
            // JUST IGNORE A DUMB USER SPAMMING DELETE
        }
    }

    private String getInput() {
        return this.input_text.getText().toString();
    }

    private boolean endsWithOperator() {
        return getInput().endsWith("+") || getInput().endsWith("-") || getInput().endsWith("÷") || getInput().endsWith("x");
    }

    private void replace(String str) {
        input_text.getText().replace(getInput().length() - 1, getInput().length(), str);
    }

    private void replace(String match, String str) {
        input_text.getText().replace(getInput().length() - match.length(), getInput().length(), str);
    }
    public void calculate_result(View v) {
        String input = getInput();

        if(!endsWithOperator()) {

            if (input.contains("x")) {
                input = input.replaceAll("x", "*");
            }

            if (input.contains("÷")) {
                input = input.replaceAll("÷", "/");
            }

            try {
                Expression expression = new ExpressionBuilder(input).build();
                double result = expression.evaluate();
                display_text.setText(String.valueOf(result));
            } catch (ArithmeticException | IllegalArgumentException e){
                display_text.setText(e.getMessage());
            }

        }
        else display_text.setText("");

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("last_result", display_text.getText().toString());
    }
}