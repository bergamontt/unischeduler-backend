# unischeduler-backend
This repository contains the source code for the backend of the UNIscheduler project.

## Run locally
To build and run the backend you first need to have Docker installed locally.
The project contains a multi-stage `Dockerfile` that builds and runs the source code of this repository.
There is also a `docker-compose` file, that uses said `Dockerfile` to run both the project and the `Postgres` database.

### Instruction:
1. Clone the project
    ```shell
    git clone https://github.com/bergamontt/unischeduler-backend.git
    cd unischeduler-backend
    ```
2. Configure the `.env` file (See `.env.example`).
3. Run the backend with `docker-compose`:
    ```shell
    docker compose up -d
    ```
The backend will be running at: `http://localhost:8080/api`

## Architecture
`Speciality` is an entity that contains data about some of the university specialities;

`User` is an entity that contains data about the student or admin;

`Course` is an entity that contains data about some course in the university;

`CourseClass` is an entity that contains data about some class of some course.
The data about the class is provided from the university schedule;

`ClassOverride` is an entity that contains overridden data about some class by some student.
It is created personally for the user that wants to override some data in the `CourseClass` provided by university;

`PersonalEvent` is an entity that contains data about the personal event created by user in their calendar;

`CalendarEvent` is a projection of `CourseClass`, `ClassOverride` and `PersonalEvent` classes.
This class is used in the `CalendarController` and `CalendarService` to represent a general event of the user's calendar;

