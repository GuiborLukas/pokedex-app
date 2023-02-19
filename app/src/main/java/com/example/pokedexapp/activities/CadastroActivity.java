package com.example.pokedexapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.pokedexapp.R;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Usuario;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {
    private EditText editTextCadastroNome, editTextCadastroTipo, editTextHabilidade1, editTextHabilidade2, editTextHabilidade3;
    private ImageView imageViewCadastroFoto;
    private Usuario user;
    private Gson gson = new Gson();
    private Uri selectedImage;
    private Bitmap imagem;

    private static final int PICK_IMAGE_REQUEST = 1307;
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

        imageViewCadastroFoto.setOnClickListener(v -> selectImage());
    }

    private void selectImage() {
        verifyPermissions(CadastroActivity.this);
        final CharSequence[] options = {"Tirar Foto", "Escolher da Galeria", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
        builder.setTitle("Adicionar foto!");
        builder.setItems(options, (dialog, which) -> {
            if ("Tirar Foto".equals(options[which])) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncherTakePicture.launch(intent);
            } else if ("Escolher da Galeria".equals(options[which])) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncherPickPicture.launch(intent);
            } else if ("Cancelar".equals(options[which])) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    ActivityResultLauncher<Intent> activityResultLauncherTakePicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    imagem = (Bitmap) bundle.get("data");
                    imageViewCadastroFoto.setImageBitmap(imagem);
                }
            }
    );

    ActivityResultLauncher<Intent> activityResultLauncherPickPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    selectedImage = result.getData().getData();
                    Glide.with(this).load(selectedImage).into(imageViewCadastroFoto);
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );


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

    public void salvarPokemon(View view) throws IOException {
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            novoPokemon.setFoto(Base64.getEncoder().encodeToString(bitmapdata));
        }
        novoPokemon.setUsuario(user.getId());

        Call<Pokemon> cadastrarPokemon = new RetrofitConfig().getPokemonsService().cadastrarPokemon(novoPokemon);
        cadastrarPokemon.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(CadastroActivity.this)
                            .setTitle("Sucesso")
                            .setMessage("Novo pokemon salvo com sucesso")
                            .setOnDismissListener(dialog -> recarregaDashboard())
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
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}