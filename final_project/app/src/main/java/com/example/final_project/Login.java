package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText id = findViewById(R.id._id1);
        EditText name = findViewById(R.id._name1);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newid = id.getText().toString();
                String newname = name.getText().toString();
                if(newid.equals("")){
                    Toast.makeText(Login.this, "用户id不能为空！", Toast.LENGTH_SHORT).show();
                }
                else{
                    Constants.STUDENT_ID = newid;
                    Constants.USER_NAME = newname;
                    Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
