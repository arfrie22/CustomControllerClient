package com.github.arfrie22.controller;

class ScanResults {
    CustomController[] validControllers;
    CustomController[] invalidControllers;
    CustomController[] allControllers;

    protected ScanResults(CustomController[] validControllers, CustomController[] invalidControllers) {
        this.validControllers = validControllers;
        this.invalidControllers = invalidControllers;

        this.allControllers = new CustomController[validControllers.length + invalidControllers.length];
        System.arraycopy(validControllers, 0, allControllers, 0, validControllers.length);
        System.arraycopy(invalidControllers, 0, allControllers, validControllers.length, invalidControllers.length);
    }

    public CustomController[] getValidControllers() {
        return  validControllers;
    }

    public CustomController[] getInvalidControllers() {
        return invalidControllers;
    }

    public CustomController[] getAllControllers() {
        return allControllers;
    }
}
