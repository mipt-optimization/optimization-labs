package ru.sberbank.demo4;

public class Employee {
    private int ID;
    private String name;
    private int age;
    private static int nextId = 1;

    public Employee() {
        this.name = "default";
        this.age = 0;
        this.ID = nextId++;
    }

    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
        this.ID = nextId++;
    }

    public void show() {
        System.out.println("Id=" + ID + "\nName=" + name + "\nAge=" + age);
    }

    public void showNextId() {
        System.out.println("Next employee id will be=" + nextId);
    }

    public static void main(String[] args) throws InterruptedException {
        boolean enableStats = false;
        Employee E = new Employee("EMPLOYEE1", 56);
        Employee F = new Employee("EMPLOYEE2", 45);
        Employee G = new Employee("EMPLOYEE3", 25);
        F = null;

        {
            Employee X = new Employee("EMPLOYEE4", 23);
            Employee Y = new Employee("EMPLOYEE5", 21);
            for (int i = 6; i < 100000; i++) {
                Y = getNewEmployee(i, "EMPLOYEE" + i);
            }
            Y.showNextId();
            X = Y = null;
            System.gc();
            System.out.println("--------------SLEEP--------------");
            Thread.sleep(10l);
        }
        E.showNextId();
    }

    private static Employee getNewEmployee(int i, String s) {
        return new Employee(s, i);
    }

    private static void printMemorySize() {
        final long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.printf("Max memory: %d MB\n", maxMemory / 1024 / 1024);
    }
}


