package com.example.test;

public class cla1 implements inter1{
    @Override
    public void say() {
        System.out.println("say");
    }

    @Override
    public void speak() {
        System.out.println("speak");
    }

    class test implements inter1{

        @Override
        public void say() {

        }

        @Override
        public void speak() {

        }
    }

}
