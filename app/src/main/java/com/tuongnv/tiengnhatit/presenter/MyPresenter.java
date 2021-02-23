package com.tuongnv.tiengnhatit.presenter;

import com.tuongnv.tiengnhatit.manager.UserManager;

public class MyPresenter {

    public MyPresenter(){
        UserManager.getInstance().addUser("tuongnv","abc13579");
    }

    public boolean login(String userName, String password){
        return UserManager.getInstance().validInformation(userName, password);
    }

}
