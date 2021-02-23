package com.tuongnv.tiengnhatit.manager;

import com.tuongnv.tiengnhatit.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private List <User> listUser;

    private UserManager(){


    }

    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
         return instance;
    }
    public void addUser(String userName, String password){
        if(listUser == null)
            listUser = new ArrayList<>();


        listUser.add(new User(userName,password));
    }

    public boolean validInformation(String userName, String password){
        for(int i = 0; i < listUser.size(); i++){
            if(listUser.get(i).getUserName().equals(userName)
                    && listUser.get(i).getPassword().equals(password))
                return true;
        }
            return false;
        }
        
}



