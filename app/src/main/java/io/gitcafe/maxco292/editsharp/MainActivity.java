package io.gitcafe.maxco292.editsharp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.gitcafe.maxco292.editsharp.db.DatabaseAdapter;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String[] JAVA_LITERAL_NAMES = {
            "abstract", "assert", "boolean", "break", "byte", "case",
            "catch", "char", "class", "const", "continue", "default",
            "do", "double", "else", "enum", "extends", "final", "finally",
            "float", "for", "if", "goto", "implements", "import", "instanceof",
            "int", "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while",
            "null"
    };
    private String[] LUA_LITERAL_NAMES = {
            "break", "goto", "do", "end", "while", "repeat",
            "until", "if", "then", "elseif", "else", "for", "in",
            "function", "local", "return",  "nil", "false",
            "true"
    };
    private String[] PY3_LITERAL_NAMES = {
            "def", "return", "raise", "from", "import", "as", "global",
            "nonlocal", "assert", "if", "elif", "else", "while", "for",
            "in", "try", "finally", "with", "except", "lambda", "or",
            "and", "not", "is", "None", "True", "False", "class", "yield",
            "del", "pass", "continue", "break"
    };
    private   String[] CS_LITERAL_NAMES = {
            "abstract",
            "add", "alias", "__arglist", "as", "ascending", "base", "bool",
            "break", "by", "byte", "case", "catch", "char", "checked",
            "class", "const", "continue", "decimal", "default", "delegate",
            "descending", "do", "double", "dynamic", "else", "enum", "equals",
            "event", "explicit", "extern", "false", "finally", "fixed",
            "float", "for", "foreach", "from", "get", "goto", "group",
            "if", "implicit", "in", "int", "interface", "internal", "into",
            "is", "join", "let", "lock", "long", "namespace", "new",
            "null", "object", "on", "operator", "orderby", "out", "override",
            "params", "partial", "private", "protected", "public", "readonly",
            "ref", "remove", "return", "sbyte", "sealed", "select", "set",
            "short", "sizeof", "stackalloc", "static", "string", "struct",
            "switch", "this", "throw", "true", "try", "typeof", "uint",
            "ulong", "unchecked", "unsafe", "ushort", "using", "virtual",
            "void", "volatile", "where", "while", "yield"
    };
    private String[] CPP_LITERAL_NAMES = {
            "alignas", "alignof", "asm", "auto", "bool", "break",
            "case", "catch", "char", "char16_t", "char32_t", "class",
            "const", "constexpr", "const_cast", "continue", "decltype",
            "default", "delete", "do", "double", "dynamic_cast", "else",
            "enum", "explicit", "export", "extern", "false", "final",
            "float", "for", "friend", "goto", "if", "inline", "int",
            "long", "mutable", "namespace", "new", "noexcept", "nullptr",
            "operator", "override", "private", "protected", "public", "register",
            "reinterpret_cast", "return", "short", "signed", "sizeof", "static",
            "static_assert", "static_cast", "struct", "switch", "template",
            "this", "thread_local", "throw", "true", "try", "typedef",
            "typeid", "typename", "union", "unsigned", "using", "virtual",
            "void", "volatile", "wchar_t", "while"
    };
    private int FILE_SELECT_CODE=0;
    private static final int MSG_SUCCESS=1;
    private static final int MSG_FAILURE=0;
    private static final int MSG_OPENFILE=2;
    private static final int MSG_DOWN_FILE_FAIL=3;
    private FloatingActionButton fab;
    private FloatingActionButton fab_git;
    private MaterialDialog materialDialog;
    private ListView listView;
    private String mPreValue[]={"0","1","2","3","4"};
    private String mDownFileName;
    private List<String> getListArray(String[] array) {
        List<String> titleArray = new ArrayList<>();
        Collections.addAll(titleArray, array);
        return titleArray;
    }
    private void setmPreValue(String[] str)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        str[0]=prefs.getString("remote_IP_text","");
        str[1]=prefs.getString("remote_Directory_text","");
        str[2]=prefs.getString("remote_username_text","");
        str[3]=prefs.getString("remote_passwords_text","");
        str[4]=prefs.getString("local_directory_text","");
    }
    Runnable mRunableGetFileList=new Runnable() {
        @Override
        public void run() {
            String[] filenames;
            final ArrayAdapter<String> arrayAdapter
                    = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1);
            try {
                filenames= SSFTPsync.connsshSftp(mPreValue[0],
                        mPreValue[2],
                        mPreValue[3],
                        -1,
                        mPreValue[1]);
                for(String s:filenames)
                {
                    arrayAdapter.add(s);
                }
                mHandler.obtainMessage(MSG_SUCCESS,arrayAdapter).sendToTarget();
            } catch (Exception e) {
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                e.printStackTrace();
            }
        }
    };
    Runnable mRunableDownLoadFile=new Runnable() {
        @Override
        public void run() {
            File fi=new File(Environment
                    .getExternalStorageDirectory().getPath()
                    +mPreValue[4]);
            if  (!fi .exists()  && !fi .isDirectory())
            {
                System.out.println("//不存在");
                fi .mkdir();
            }
            File fiLF=new File(fi.getPath()+File.separator+ mDownFileName);
            Log.d("fileLOCALPATH",fiLF.toString());
            if(!fiLF.exists())
            {
                try {
                    fiLF.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                SSFTPsync.sshSftpDOWN(
                        mPreValue[0],
                        mPreValue[2],
                        mPreValue[3],
                        -1,
                        mPreValue[1],
                        fiLF.toString(),
                        mDownFileName
                );
                mHandler.obtainMessage(MSG_OPENFILE,fiLF.toString()).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(MSG_DOWN_FILE_FAIL,fiLF.toString()).sendToTarget();
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    materialDialog.dismiss();
                    materialDialog=new MaterialDialog(MainActivity.this).setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    listView=new ListView(MainActivity.this);
                    listView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    float scale = getResources().getDisplayMetrics().density;
                    int dpAsPixels = (int) (8 * scale + 0.5f);
                    listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
                    listView.setDividerHeight(0);
                    listView.setAdapter((ArrayAdapter<String>) msg.obj);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mDownFileName = parent.getItemAtPosition(position).toString();
                            new Thread(mRunableDownLoadFile).start();
                        }
                    });
                    materialDialog.setTitle("文件列表获取成功").setContentView(listView);
                    materialDialog.show();
                    Toast.makeText(getApplication(), "成功获取文件列表", Toast.LENGTH_SHORT).show();
                    break;

                case MSG_FAILURE:
                    materialDialog.dismiss();
                    Toast.makeText(getApplication(), "获取文件列表失败，请重试", Toast.LENGTH_SHORT).show();
                    materialDialog=new MaterialDialog(MainActivity.this).setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.setTitle("获取文件列表失败，请重试");
                    materialDialog.setNegativeButton("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            materialDialog=new MaterialDialog(MainActivity.this);
                            materialDialog.setTitle("文件列表获取中");
                            materialDialog.setContentView(new ListView(MainActivity.this));
                            materialDialog.show();
                            new Thread(mRunableGetFileList).start();
                        }
                    });
                    materialDialog.show();
                    break;
                case MSG_OPENFILE:
                    String file=msg.obj.toString();
                    SelectLANG(Uri.parse(file));
                    materialDialog.dismiss();
                    Log.d("uritest",file);
                    break;
                case MSG_DOWN_FILE_FAIL:
                    materialDialog.dismiss();
                    Toast.makeText(getApplication(), "尝试下载文件失败，请重试", Toast.LENGTH_SHORT).show();
                    materialDialog=new MaterialDialog(MainActivity.this).setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.setTitle("尝试下载文件失败，请重试");
                    materialDialog.setNegativeButton("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            materialDialog = new MaterialDialog(MainActivity.this);
                            materialDialog.setTitle("文件列表获取中");
                            materialDialog.setContentView(new ListView(MainActivity.this));
                            materialDialog.show();
                            new Thread(mRunableDownLoadFile).start();
                        }
                    });
                    materialDialog.show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri=getIntent().getData();
        setmPreValue(mPreValue);

//        final EditText editText = new EditText(this);
//        editText.setText("请输入要同步文件名，服务器设置请到settings设置");
//        materialDialog=new MaterialDialog(this)
//                .setView(editText)
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                String RemoteFileName=editText.getText().toString();
//                                File fi=new File(Environment
//                                        .getExternalStorageDirectory().getPath()
//                                        +mPreValue[4]);
//                                if  (!fi .exists()  && !fi .isDirectory())
//                                {
//                                    System.out.println("//不存在");
//                                    fi .mkdir();
//                                }
//                                File fiLF=new File(fi.getPath()+File.separator+ RemoteFileName);
//                                Log.d("fileLOCALPATH",fiLF.toString());
//                                if(!fiLF.exists())
//                                {
//                                    try {
//                                        fiLF.createNewFile();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                try {
//                                    SSFTPsync.sshSftpDOWN(
//                                            mPreValue[0],
//                                            mPreValue[2],
//                                            mPreValue[3],
//                                            -1,
//                                            mPreValue[1],
//                                            fiLF.toString(),
//                                            RemoteFileName
//                                            );
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
//
//                        Toast.makeText(MainActivity.this,editText.getText().toString()+"已同步到本地"+mPreValue[4]+"文件夹" ,Toast.LENGTH_SHORT).show();
//                        materialDialog.dismiss();
//                    }
//                })
//                .setNegativeButton("CANCL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        materialDialog.dismiss();
//                    }
//                });

        listView = new ListView(this);

        //materialDialog=new MaterialDialog(this);

        // 向数据库中插入指定数据

        if(uri!=null)
        {
            SelectLANG(uri);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
//                intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab_git= (FloatingActionButton) findViewById(R.id.fab_git);
        fab_git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog=new MaterialDialog(MainActivity.this);
                materialDialog.setTitle("文件列表获取中");
                materialDialog.setContentView(new ListView(MainActivity.this));
                materialDialog.show();
                new Thread(mRunableGetFileList).start();
            }
        });
        fab_git.hide();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILE_SELECT_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                Uri uri=data.getData();
                SelectLANG(uri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent stsettings=new Intent(this,SettingsActivity.class);
            startActivity(stsettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_git) {

            fab.hide();
            fab_git.show();

        } else if (id == R.id.nav_external_storage) {
            fab_git.hide();
            fab.show();
        }else if(id==R.id.nav_contactsme)
        {
            String[] email = {"maxco292@hotmail.com"}; // 需要注意，email必须以数组形式传入
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822"); // 设置邮件格式
            intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
//            intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
            intent.putExtra(Intent.EXTRA_SUBJECT, "EditSharp反馈"); // 主题
//            intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
            startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void SelectLANG(Uri uri)
    {
        DatabaseAdapter.getIntance(this).deleteAll();
        Intent i=new Intent(this,Editor.class);
        i.putExtra("filedata",uri.getPath());
        String filename=uri.getLastPathSegment();
        int pos=filename.lastIndexOf('.');
        if (pos>0)
        {
            String type=filename.subSequence(pos+1,filename.length()).toString();
            i.putExtra("type",type);
            switch(type)
            {
                case "py":
                    Toast.makeText(MainActivity.this,"python文件",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(PY3_LITERAL_NAMES));
                        }
                    }).start();
                    startActivity(i);
                    break;
                case "cpp":
                    Toast.makeText(MainActivity.this,"c++文件",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(CPP_LITERAL_NAMES));
                        }
                    }).start();
                    startActivity(i);
                    break;
                case "h":
                    Toast.makeText(MainActivity.this,"C++/C headers文件",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(CPP_LITERAL_NAMES));
                        }
                    }).start();
                    startActivity(i);
                    break;
                case "cs":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(CS_LITERAL_NAMES));
                        }
                    }).start();
                    Toast.makeText(MainActivity.this,"CSHARP文件",Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    break;
                case "lua":
                    Toast.makeText(MainActivity.this,"lua文件",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(LUA_LITERAL_NAMES));
                        }
                    }).start();
                    startActivity(i);
                    break;
                case "java":
                    Toast.makeText(MainActivity.this,"java文件",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(JAVA_LITERAL_NAMES));
                        }
                    }).start();
                    startActivity(i);
                    break;
                case "c":
                    Toast.makeText(MainActivity.this,"c文件",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(MainActivity.this).inserInfo(getListArray(CPP_LITERAL_NAMES));
                        }
                    }).start();
                    startActivity(i);
                    break;
                default:
                    Toast.makeText(MainActivity.this,"请选择正确的代码文件",Toast.LENGTH_SHORT).show();
                    break;
            }
        }else
        {
            Toast.makeText(MainActivity.this,"请选择正确的代码文件",Toast.LENGTH_SHORT).show();
        }
    }
}
