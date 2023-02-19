package com.example.pokedexapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pokedexapp.R;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Usuario;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {
    EditText editTextCadastroNome, editTextCadastroTipo, editTextHabilidade1, editTextHabilidade2, editTextHabilidade3;
    ImageView imageViewCadastroFoto;
    Usuario user;

    private Uri selectedImage;
    String imagem;

    private static final int PICK_IMAGE_REQUEST = 1307;
    private static final int TAKE_PHOTO_REQUEST = 0304;

    private static final int REQUEST_PERMISSIONS = 0402;
    private static final String[] PERMISSIONS_TO_REQUEST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Bundle bundle = getIntent().getExtras();
        user = (Usuario) bundle.getSerializable("usuario");

        editTextCadastroNome = findViewById(R.id.editTextCadastroNome);
        editTextCadastroTipo = findViewById(R.id.editTextCadastroTipo);
        editTextHabilidade1 = findViewById(R.id.editTextCadastroHabilidade1);
        editTextHabilidade2 = findViewById(R.id.editTextCadastroHabilidade2);
        editTextHabilidade3 = findViewById(R.id.editTextCadastroHabilidade3);
        imageViewCadastroFoto = findViewById(R.id.imageViewCadastroFoto);

        imageViewCadastroFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        verifyPermissions(CadastroActivity.this);
        final CharSequence[] options = {"Tirar Foto", "Escolher da Galeria", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
        builder.setTitle("Adicionar foto!");
        builder.setItems(options, (dialog, which) -> {
            if ("Tirar Foto".equals(options[which])) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(cameraIntent,TAKE_PHOTO_REQUEST);
            } else if ("Escolher da Galeria".equals(options[which])) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else if ("Cancelar".equals(options[which])) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Bitmap bitmap = null;
                try {
                    selectedImage = data.getData();
                    Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                    cursor.moveToFirst();
                    String documentId = cursor.getString(0);
                    documentId = documentId.substring(documentId.lastIndexOf(":") + 1);
                    cursor.close();

                    cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ?", new String[]{documentId}, null);
                    cursor.moveToFirst();
                    imagem = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    Log.i("TESTE", "onActivityResult: " + imagem);
                    cursor.close();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {

                }
                imageViewCadastroFoto.setImageBitmap(bitmap);
            } else if (requestCode == TAKE_PHOTO_REQUEST) {

                File foto = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : foto.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        Log.i("FOTO CAMERA", "onActivityResult 0: " + temp.getName());
                        foto = temp;
                        break;
                    }
                }
                try {
                    Log.i("FOTO CAMERA", "onActivityResult 1: " + foto.getAbsolutePath());
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(foto.getAbsolutePath(),
                            bitmapOptions);
                    imageViewCadastroFoto.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Foto" + File.separator + "Mutant";
                    String filename = System.currentTimeMillis() + ".jpg";
                    Log.i("FOTO CAMERA", "onActivityResult 2: " + filename);

                    imagem = foto.getAbsolutePath();
                    Log.i("FOTO CAMERA", "onActivityResult 3: " + imagem);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void verifyPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if ((permission != PackageManager.PERMISSION_GRANTED) || (cameraPermission != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_TO_REQUEST,
                    REQUEST_PERMISSIONS
            );
        }
    }

    public void salvarPokemon(View view) {
        if (editTextCadastroNome.length() == 0) {
            Toast.makeText(this, "Nome não pode ser vazio!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextCadastroTipo.length() == 0) {
            Toast.makeText(this, "Tipo não pode ser vazio!", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((editTextHabilidade1.length() == 0) && (editTextHabilidade2.length() == 0) && (editTextHabilidade3.length() == 0)) {
            Toast.makeText(this, "Informe ao menos uma habilidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        Pokemon novoPokemon = new Pokemon();
        novoPokemon.setNome(editTextCadastroNome.getText().toString());
        novoPokemon.setTipo(editTextCadastroTipo.getText().toString());
        List<String> habilidades = new ArrayList<>();
        habilidades.add(editTextHabilidade1.getText().toString());
        habilidades.add(editTextHabilidade2.getText().toString());
        habilidades.add(editTextHabilidade3.getText().toString());
        novoPokemon.setHabilidade(habilidades);
        File file = new File(imagem);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, blob);
        byte[] bitmapdata = blob.toByteArray();
        novoPokemon.setFoto(bitmapdata);
        novoPokemon.setUsuario(user.getId());

        Gson gson = new Gson();
        String jsonPokemon = gson.toJson(novoPokemon);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonPokemon);

        Call<Pokemon> cadastrarPokemon = new RetrofitConfig().getPokemonsService().cadastrarPokemon(requestBody);
        cadastrarPokemon.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(CadastroActivity.this)
                            .setTitle("Sucesso")
                            .setMessage("Novo pokemon salvo com sucesso")
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    recarregaDashboard();
                                }
                            })
                            .show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        new AlertDialog.Builder(CadastroActivity.this).setTitle("Erro").setMessage(jObjError.getString("error")).show();
                    } catch (JSONException | IOException e) {
                        new AlertDialog.Builder(CadastroActivity.this).setTitle("Erro").setMessage("Erro interno.").show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.e("ERRO", "Cadastrar Pokemon: " + t.getMessage());
                new AlertDialog.Builder(CadastroActivity.this).setTitle("Erro interno").setMessage("Erro ao conectar ao servidor.").show();
            }
        });
    }

    private void recarregaDashboard() {
        Intent intent = new Intent(CadastroActivity.this, DashboardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}