
package com.example.pokedexapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usuario implements Serializable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("senha")
    @Expose
    private String senha;

    /**
     * No args constructor for use in serialization
     */
    public Usuario() {
    }

    /**
     * @param id
     * @param login
     * @param senha
     */
    public Usuario(Long id, String login, String senha) {
        super();
        this.id = id;
        this.login = login;
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Usuario.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("login");
        sb.append('=');
        sb.append(((this.login == null) ? "<null>" : this.login));
        sb.append(',');
        sb.append("senha");
        sb.append('=');
        sb.append(((this.senha == null) ? "<null>" : this.senha));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
