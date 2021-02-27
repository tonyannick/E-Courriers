package dcsibudget.model;

import org.primefaces.model.StreamedContent;

import java.util.List;

public class User {

    private String userId;
    private String userlogin;
    private String userName;
    private String userPrenom;
    private String userMail;
    private String userTel;
    private String userService;
    private String userPseudo;
    private String userPassword;
    private String userDirection;
    private String userFonction;
    private String userEtablissement;
    private String userProfil;
    private String userType;
    private String userEtat;
    private StreamedContent userPhoto;
    private boolean seSouvenir;
    private List<User> userList;

    public User() {
    }

    public User(String id, String nom, String fonction) {
        this.userId = id;
        this.userName = nom;
        this.userFonction = fonction;
    }


    public User(String userId, String userlogin, String userName, String userMail, String userTel,
                String userPassword, String userDirection, String userFonction, String userProfil, String userType,
                String userEtat) {
        this.userId = userId;
        this.userlogin = userlogin;
        this.userName = userName;
        this.userMail = userMail;
        this.userTel = userTel;
        this.userPassword = userPassword;
        this.userDirection = userDirection;
        this.userFonction = userFonction;
        this.userProfil = userProfil;
        this.userType = userType;
        this.userEtat = userEtat;
    }

    public User(String userId, String userlogin, String userName, String userMail, String userTel,
                String userDirection, String userFonction, String userProfil, String userEtat) {
        this.userId = userId;
        this.userlogin = userlogin;
        this.userName = userName;
        this.userMail = userMail;
        this.userTel = userTel;
        this.userDirection = userDirection;
        this.userFonction = userFonction;
        this.userProfil = userProfil;
        this.userEtat = userEtat;
    }


    public StreamedContent getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(StreamedContent userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserEtat() {
        return userEtat;
    }

    public void setUserEtat(String userEtat) {
        this.userEtat = userEtat;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserProfil() {
        return userProfil;
    }

    public void setUserProfil(String userProfil) {
        this.userProfil = userProfil;
    }

    public String getUserEtablissement() {
        return userEtablissement;
    }

    public void setUserEtablissement(String userEtablissement) {
        this.userEtablissement = userEtablissement;
    }

    public String getUserPrenom() {
        return userPrenom;
    }

    public void setUserPrenom(String userPrenom) {
        this.userPrenom = userPrenom;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getUserPseudo() {
        return userPseudo;
    }

    public void setUserPseudo(String userPseudo) {
        this.userPseudo = userPseudo;
    }

    public String getUserFonction() {
        return userFonction;
    }

    public void setUserFonction(String userFonction) {
        this.userFonction = userFonction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSeSouvenir() {
        return seSouvenir;
    }

    public void setSeSouvenir(boolean seSouvenir) {
        this.seSouvenir = seSouvenir;
    }

    public String getUserlogin() {
        return userlogin;
    }

    public void setUserlogin(String userlogin) {
        this.userlogin = userlogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserDirection() {
        return userDirection;
    }

    public void setUserDirection(String userDirection) {
        this.userDirection = userDirection;
    }

    public String getUserService() {
        return userService;
    }

    public void setUserService(String userService) {
        this.userService = userService;
    }
}
