package com.kry.WebApp.domainobject;

public enum BackendEndPoints {
    getAllServices,updateService,addService,removeService;

    private String address;

    static{
        getAllServices.address = "/services/getAllServices";
        updateService.address = "/services/update";
        addService.address = "/services/add";
        removeService.address = "/services/removebyid/{id}";
    }

    public String getAddress() {
        return address;
    }
}
