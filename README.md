# Java Code Generation Engine (JCGE)

Java Code Generation Engine (JCGE) is a Maven plugin designed to automatically generate Entity or DTO classes from
YAML/JSON files.
This plugin simplifies the process of creating model classes required for Java applications,
reducing time and coding errors.

---

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [YAML File Examples](#yaml-file-examples)
- [Generating Classes](#generating-classes)
- [Caveat](#caveat)
- [Development Setup](#development-setup)
- [Future Implementations](#future-implementations)
- [Requirements](#requirements)
- [Contributing](#contributing)
- [License](#license)
- [Authors](#authors)
- [Repository](#repository)

---

## Features

- Automatic generation of Entity or DTO classes from YAML/JSON files.
- Automatic generation of: Fields, NoArgsConstructor, Getters, Setters, Builder, Equals, HashCode, ToString
- Automatic generation of: JPA, Jackson, JSR-303 annotations.
- Support for all Java types

---

## Installation

To include JCGE in your Maven project, add the following plugin section to your `pom.xml` file:

```xml

<build>
  <plugins>
    <plugin>
      <groupId>io.github.robertomanfreda</groupId>
      <artifactId>jcge-maven-plugin</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </plugin>
  </plugins>
</build>
```

Depending on what you use, you need to import the appropriate dependencies:

- If you use Jakarta for validation and persistence, then you should import:

```xml

<dependencies>
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
    </dependency>
    <dependency>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
    </dependency>
</dependencies>
```

- If you use javax for validation and persistence, then you should import:

```xml

<dependencies>
    <dependency>
        <groupId>javax.persistence</groupId>
        <artifactId>javax.persistence-api</artifactId>
    </dependency>
    <dependency>
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
    </dependency>
</dependencies>
```

- If you use Jackson, then you should import:

```xml

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

---

## Usage

You can configure the plugin to load local or remote YAML/JSON files.

- For local configuration, you can specify single files:

```xml

<configuration>
    <localFiles>
        <file>${project.basedir}/src/main/resources/dto/student.yaml</file>
        <file>${project.basedir}/src/main/resources/entity/student.yaml</file>
        <file>${project.basedir}/src/main/resources/dto/person.yaml</file>
    </localFiles>
</configuration>
```

or entire folders:

```xml

<configuration>
    <localFolders>
        <folder>${project.basedir}/src/main/resources/dto</folder>
        <folder>${project.basedir}/src/main/resources/entity</folder>
    </localFolders>
</configuration>
```

- For remote configuration, you can specify single files:

```xml

<configuration>
    <remoteFiles>
        <file>https://raw.githubusercontent.com/username/repo-name/branch-name/dto/student.yaml</file>
        <file>https://raw.githubusercontent.com/username/repo-name/branch-name/entity/student.yaml</file>
        <file>https://raw.githubusercontent.com/username/repo-name/branch-name/dto/person.yaml</file>
    </remoteFiles>
</configuration>
```

or entire remote folders:

```xml

<configuration>
    <remoteFolders>
        <folder>https://raw.githubusercontent.com/username/repo-name/branch-name/dto</folder>
        <folder>https://raw.githubusercontent.com/username/repo-name/branch-name/entity</folder>
    </remoteFolders>
</configuration>
```

---

## YAML File Examples:

- A DTO with Jackson annotations using jakarta imports

```yaml
---
title: StudentDTO
version: jakarta
description: The StudentDTO class with various properties and Jackson annotations
generationType: dto
jackson:
  - JsonIgnoreProperties({"password", "name", "foo", "ssn"})
  - JsonInclude(JsonInclude.Include.NON_NULL)
  - JsonPropertyOrder({"id", "firstName", "lastName", "email", "dateOfBirth", "enrolledCourses", "address", "phoneNumber", "gender", "nationality", "emergencyContact", "listListIds", "mapField"})
properties:
  id:
    type: Integer
    description: The unique identifier for a student
  firstName:
    type: String
    description: The first name of the student
    jackson:
      - JsonProperty("user_name")
  lastName:
    type: String
    description: The last name of the student
  email:
    type: String
    description: The email address of the student
  password:
    type: String
    description: The password for the student account
    jackson:
      - JsonIgnore
  ssn:
    type: String
    description: The social security number of the student
    jackson:
      - JsonIgnore
  dateOfBirth:
    type: Date
    description: The date of birth of the student
  gender:
    type: String
    description: The gender of the student
  nationality:
    type: String
    description: The nationality of the student
  address:
    type: String
    description: The address of the student
  phoneNumber:
    type: String
    description: The phone number of the student
  emergencyContact:
    type: String
    description: The emergency contact for the student
  enrolledCourses:
    type: List<Integer>
    description: A list of courses the student is enrolled in
  listListIds:
    type: List<List<Integer>>
    description: A list of lists of IDs
  mapField:
    type: Map<String, List<Integer>>
    description: A map with String keys and List<Integer> values
  grades:
    type: Map<String, Double>
    description: A map of course names to grades
  attendanceRecord:
    type: Map<Date, Boolean>
    description: A record of attendance with dates and presence status
  extracurricularActivities:
    type: List<String>
    description: A list of extracurricular activities the student is involved in
  notes:
    type: List<String>
    description: Additional notes about the student
  metadata:
    type: Map<String, String>
    description: Additional metadata for the student
  createdAt:
    type: Date
    description: The creation timestamp of the student record
    jackson:
      - JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
  updatedAt:
    type: Date
    description: The last updated timestamp of the student record
    jackson:
      - JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
```

- A DTO with JSR-303 annotations using javax imports

```yaml
---
title: StudentDTO
version: javax
description: The StudentDTOJsr303 class with various properties and JSR303 annotations
generationType: dto
properties:
  id:
    type: Integer
    description: The unique identifier for a student
    jsr303:
      - NotNull
  firstName:
    type: String
    description: The first name of the student
    jsr303:
      - NotBlank
      - Size(min = 1, max = 50)
  lastName:
    type: String
    description: The last name of the student
    jsr303:
      - NotBlank
      - Size(min = 1, max = 50)
  email:
    type: String
    description: The email address of the student
    jsr303:
      - NotBlank
      - Email
  password:
    type: String
    description: The password for the student account
    jsr303:
      - NotBlank
      - Size(min = 8, max = 100)
  ssn:
    type: String
    description: The social security number of the student
    jsr303:
      - Pattern(regexp = "^(?!000|666)[0-8][0-9]{2}-[0-9]{2}-[0-9]{4}$")
  dateOfBirth:
    type: Date
    description: The date of birth of the student
    jsr303:
      - NotNull
      - Past
  gender:
    type: String
    description: The gender of the student
    jsr303:
      - NotBlank
      - Pattern(regexp = "Male|Female|Other")
  nationality:
    type: String
    description: The nationality of the student
    jsr303:
      - NotBlank
  address:
    type: String
    description: The address of the student
    jsr303:
      - NotBlank
      - Size(min = 10, max = 100)
  phoneNumber:
    type: String
    description: The phone number of the student
    jsr303:
      - NotBlank
      - Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$")
  emergencyContact:
    type: String
    description: The emergency contact for the student
    jsr303:
      - NotBlank
  enrolledCourses:
    type: List<Integer>
    description: A list of courses the student is enrolled in
    jsr303:
      - NotNull
      - Size(min = 1)
  listListIds:
    type: List<List<Integer>>
    description: A list of lists of IDs
  mapField:
    type: Map<String, List<Integer>>
    description: A map with String keys and List<Integer> values
  grades:
    type: Map<String, Double>
    description: A map of course names to grades
    jsr303:
      - NotNull
  attendanceRecord:
    type: Map<Date, Boolean>
    description: A record of attendance with dates and presence status
  extracurricularActivities:
    type: List<String>
    description: A list of extracurricular activities the student is involved in
    jsr303:
      - Size(min = 0, max = 10)
  notes:
    type: List<String>
    description: Additional notes about the student
    jsr303:
      - Size(max = 500)
  metadata:
    type: Map<String, String>
    description: Additional metadata for the student
  createdAt:
    type: Date
    description: The creation timestamp of the student record
  updatedAt:
    type: Date
    description: The last updated timestamp of the student record
    jsr303:
      - PastOrPresent
```

- An entity with JPA annotations using jakarta imports

```yaml
---
title: StudentEntity
version: jakarta
description: The Student entity class with various properties and JPA annotations
generationType: entity
jpa:
  - Entity
  - Table(name = "students")
properties:
  id:
    type: Integer
    description: The unique identifier for a student
    jpa:
      - Id
      - GeneratedValue(strategy = GenerationType.IDENTITY)
  firstName:
    type: String
    description: The first name of the student
    jpa:
      - Column(name = "first_name", nullable = false, length = 50)
  lastName:
    type: String
    description: The last name of the student
    jpa:
      - Column(name = "last_name", nullable = false, length = 50)
  email:
    type: String
    description: The email address of the student
    jpa:
      - Column(nullable = false, unique = true)
  password:
    type: String
    description: The password for the student account
  ssn:
    type: String
    description: The social security number of the student
  dateOfBirth:
    type: Date
    description: The date of birth of the student
    jpa:
      - Column(name = "date_of_birth", nullable = false)
  gender:
    type: String
    description: The gender of the student
    jpa:
      - Column(nullable = false)
  nationality:
    type: String
    description: The nationality of the student
    jpa:
      - Column(nullable = false)
  address:
    type: String
    description: The address of the student
    jpa:
      - Column(nullable = false, length = 100)
  phoneNumber:
    type: String
    description: The phone number of the student
    jpa:
      - Column(name = "phone_number", nullable = false, length = 25)
  emergencyContact:
    type: String
    description: The emergency contact for the student
    jpa:
      - Column(name = "emergency_contact", nullable = false)
  enrolledCourses:
    type: List<Integer>
    description: A list of courses the student is enrolled in
  listListIds:
    type: List<List<Integer>>
    description: A list of lists of IDs
  mapField:
    type: Map<String, List<Integer>>
    description: A map with String keys and List<Integer> values
  grades:
    type: Map<String, Double>
    description: A map of course names to grades
  attendanceRecord:
    type: Map<Date, Boolean>
    description: A record of attendance with dates and presence status
  extracurricularActivities:
    type: List<String>
    description: A list of extracurricular activities the student is involved in
  notes:
    type: List<String>
    description: Additional notes about the student
  metadata:
    type: Map<String, String>
    description: Additional metadata for the student
  createdAt:
    type: Date
    description: The creation timestamp of the student record
    jpa:
      - Column(name = "created_at", updatable = false)
  updatedAt:
    type: Date
    description: The last updated timestamp of the student record
    jpa:
      - Column(name = "updated_at")
```

---

## Generating Classes

To generate classes from the YAML files you can run the following Maven command:

```sh
mvn clean com.robertomanfreda:jcge-maven-plugin:1.0.0-SNAPSHOT:generate
```

or using the short form

```sh
mvn jcge:generate
```

The plugin will process the files and generate the classes in the target/generated-sources/jcge folder.

Here is an example of a class generated from the JPA StudentEntity file above:

```java
package jcge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The Student entity class with various properties and JPA annotations
 */
@Entity
@Table(
        name = "students"
)
public class StudentEntity implements Serializable {
    /**
     * The unique identifier for a student
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    /**
     * The first name of the student
     */
    @Column(
            name = "first_name",
            nullable = false,
            length = 50
    )
    private String firstName;

    /**
     * The last name of the student
     */
    @Column(
            name = "last_name",
            nullable = false,
            length = 50
    )
    private String lastName;

    /**
     * The email address of the student
     */
    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    /**
     * The password for the student account
     */
    private String password;

    /**
     * The social security number of the student
     */
    private String ssn;

    /**
     * The date of birth of the student
     */
    @Column(
            name = "date_of_birth",
            nullable = false
    )
    private Date dateOfBirth;

    /**
     * The gender of the student
     */
    @Column(
            nullable = false
    )
    private String gender;

    /**
     * The nationality of the student
     */
    @Column(
            nullable = false
    )
    private String nationality;

    /**
     * The address of the student
     */
    @Column(
            nullable = false,
            length = 100
    )
    private String address;

    /**
     * The phone number of the student
     */
    @Column(
            name = "phone_number",
            nullable = false,
            length = 25
    )
    private String phoneNumber;

    /**
     * The emergency contact for the student
     */
    @Column(
            name = "emergency_contact",
            nullable = false
    )
    private String emergencyContact;

    /**
     * A list of courses the student is enrolled in
     */
    private List<Integer> enrolledCourses;

    /**
     * A list of lists of IDs
     */
    private List<List<Integer>> listListIds;

    /**
     * A map with String keys and List<Integer> values
     */
    private Map<String, List<Integer>> mapField;

    /**
     * A map of course names to grades
     */
    private Map<String, Double> grades;

    /**
     * A record of attendance with dates and presence status
     */
    private Map<Date, Boolean> attendanceRecord;

    /**
     * A list of extracurricular activities the student is involved in
     */
    private List<String> extracurricularActivities;

    /**
     * Additional notes about the student
     */
    private List<String> notes;

    /**
     * Additional metadata for the student
     */
    private Map<String, String> metadata;

    /**
     * The creation timestamp of the student record
     */
    @Column(
            name = "created_at",
            updatable = false
    )
    private Date createdAt;

    /**
     * The last updated timestamp of the student record
     */
    @Column(
            name = "updated_at"
    )
    private Date updatedAt;

    public StudentEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StudentEntity that = (StudentEntity) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.ssn, that.ssn) &&
                Objects.equals(this.dateOfBirth, that.dateOfBirth) &&
                Objects.equals(this.gender, that.gender) &&
                Objects.equals(this.nationality, that.nationality) &&
                Objects.equals(this.address, that.address) &&
                Objects.equals(this.phoneNumber, that.phoneNumber) &&
                Objects.equals(this.emergencyContact, that.emergencyContact) &&
                Objects.equals(this.enrolledCourses, that.enrolledCourses) &&
                Objects.equals(this.listListIds, that.listListIds) &&
                Objects.equals(this.mapField, that.mapField) &&
                Objects.equals(this.grades, that.grades) &&
                Objects.equals(this.attendanceRecord, that.attendanceRecord) &&
                Objects.equals(this.extracurricularActivities, that.extracurricularActivities) &&
                Objects.equals(this.notes, that.notes) &&
                Objects.equals(this.metadata, that.metadata) &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, ssn, dateOfBirth, gender, nationality, address, phoneNumber, emergencyContact, enrolledCourses, listListIds, mapField, grades, attendanceRecord, extracurricularActivities, notes, metadata, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "StudentEntity{" +
                "id='" + id + '\'' + ", " +
                "firstName='" + firstName + '\'' + ", " +
                "lastName='" + lastName + '\'' + ", " +
                "email='" + email + '\'' + ", " +
                "password='" + password + '\'' + ", " +
                "ssn='" + ssn + '\'' + ", " +
                "dateOfBirth='" + dateOfBirth + '\'' + ", " +
                "gender='" + gender + '\'' + ", " +
                "nationality='" + nationality + '\'' + ", " +
                "address='" + address + '\'' + ", " +
                "phoneNumber='" + phoneNumber + '\'' + ", " +
                "emergencyContact='" + emergencyContact + '\'' + ", " +
                "enrolledCourses='" + enrolledCourses + '\'' + ", " +
                "listListIds='" + listListIds + '\'' + ", " +
                "mapField='" + mapField + '\'' + ", " +
                "grades='" + grades + '\'' + ", " +
                "attendanceRecord='" + attendanceRecord + '\'' + ", " +
                "extracurricularActivities='" + extracurricularActivities + '\'' + ", " +
                "notes='" + notes + '\'' + ", " +
                "metadata='" + metadata + '\'' + ", " +
                "createdAt='" + createdAt + '\'' + ", " +
                "updatedAt='" + updatedAt + '\'' + "}";
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSsn() {
        return ssn;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getNationality() {
        return nationality;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public List<Integer> getEnrolledCourses() {
        return enrolledCourses;
    }

    public List<List<Integer>> getListListIds() {
        return listListIds;
    }

    public Map<String, List<Integer>> getMapField() {
        return mapField;
    }

    public Map<String, Double> getGrades() {
        return grades;
    }

    public Map<Date, Boolean> getAttendanceRecord() {
        return attendanceRecord;
    }

    public List<String> getExtracurricularActivities() {
        return extracurricularActivities;
    }

    public List<String> getNotes() {
        return notes;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public void setEnrolledCourses(List<Integer> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public void setListListIds(List<List<Integer>> listListIds) {
        this.listListIds = listListIds;
    }

    public void setMapField(Map<String, List<Integer>> mapField) {
        this.mapField = mapField;
    }

    public void setGrades(Map<String, Double> grades) {
        this.grades = grades;
    }

    public void setAttendanceRecord(Map<Date, Boolean> attendanceRecord) {
        this.attendanceRecord = attendanceRecord;
    }

    public void setExtracurricularActivities(List<String> extracurricularActivities) {
        this.extracurricularActivities = extracurricularActivities;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;

        private String firstName;

        private String lastName;

        private String email;

        private String password;

        private String ssn;

        private Date dateOfBirth;

        private String gender;

        private String nationality;

        private String address;

        private String phoneNumber;

        private String emergencyContact;

        private List<Integer> enrolledCourses;

        private List<List<Integer>> listListIds;

        private Map<String, List<Integer>> mapField;

        private Map<String, Double> grades;

        private Map<Date, Boolean> attendanceRecord;

        private List<String> extracurricularActivities;

        private List<String> notes;

        private Map<String, String> metadata;

        private Date createdAt;

        private Date updatedAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder ssn(String ssn) {
            this.ssn = ssn;
            return this;
        }

        public Builder dateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder nationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder emergencyContact(String emergencyContact) {
            this.emergencyContact = emergencyContact;
            return this;
        }

        public Builder enrolledCourses(List<Integer> enrolledCourses) {
            this.enrolledCourses = enrolledCourses;
            return this;
        }

        public Builder listListIds(List<List<Integer>> listListIds) {
            this.listListIds = listListIds;
            return this;
        }

        public Builder mapField(Map<String, List<Integer>> mapField) {
            this.mapField = mapField;
            return this;
        }

        public Builder grades(Map<String, Double> grades) {
            this.grades = grades;
            return this;
        }

        public Builder attendanceRecord(Map<Date, Boolean> attendanceRecord) {
            this.attendanceRecord = attendanceRecord;
            return this;
        }

        public Builder extracurricularActivities(List<String> extracurricularActivities) {
            this.extracurricularActivities = extracurricularActivities;
            return this;
        }

        public Builder notes(List<String> notes) {
            this.notes = notes;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public StudentEntity build() {
            StudentEntity instance = new StudentEntity();
            instance.id = this.id;
            instance.firstName = this.firstName;
            instance.lastName = this.lastName;
            instance.email = this.email;
            instance.password = this.password;
            instance.ssn = this.ssn;
            instance.dateOfBirth = this.dateOfBirth;
            instance.gender = this.gender;
            instance.nationality = this.nationality;
            instance.address = this.address;
            instance.phoneNumber = this.phoneNumber;
            instance.emergencyContact = this.emergencyContact;
            instance.enrolledCourses = this.enrolledCourses;
            instance.listListIds = this.listListIds;
            instance.mapField = this.mapField;
            instance.grades = this.grades;
            instance.attendanceRecord = this.attendanceRecord;
            instance.extracurricularActivities = this.extracurricularActivities;
            instance.notes = this.notes;
            instance.metadata = this.metadata;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
```

---

## Caveat

The plugin is currently in an early stage and has not been officially released yet. It is still under active
development.

---

## Development Setup

At this moment to use the plugin, you need to clone the project from the main branch and compile it locally. Follow
these steps:

- Clone the repository:

```sh
git clone https://github.com/robertomanfreda/java-code-generation-engine.git
```

- Navigate to the project directory:

```sh
cd java-code-generation-engine
```

- Compile and install the plugin locally:

```sh
mvn clean install
```

After these steps, you can use the plugin in your Maven projects as reported in
the [Generating Classes](#generating-classes) section.

---

## Future Implementations

- Automated release via GitHub CI/CD
- Support for custom types: Allow the definition and usage of custom types in YAML/JSON files.
- Support for authentication for remote downloads: Implement authentication mechanisms for downloading protected remote
  files.
- Support for MapStruct: Automate the generation of mappers for converting entities to DTOs and vice versa using
  MapStruct.
  Generation of unit tests: Automate the generation of unit tests for the generated classes.
- Support for Swagger/OpenAPI: Generate DTOs from Swagger/OpenAPI specifications.
- Customizable configuration: Allow greater customization of plugin configurations through advanced parameters.
- Support for other serialization libraries: In addition to Jackson, add support for other serialization/deserialization
  libraries like Gson or JSON-B.
- Support for advanced validation models: Integrate more advanced and customizable validation models.
- Integration with CI/CD tools: Facilitate integration with continuous integration and continuous deployment (CI/CD)
  tools to further automate the code generation process.

---

## Requirements

- Java 11 or higher
- Maven 3.6.3 or higher

---

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.
For major changes, please open an issue first to discuss what you would like to change.

- Fork the repository
- Create your feature branch (git checkout -b feature/YourFeature)
- Commit your changes (git commit -am 'Add some feature')
- Push to the branch (git push origin feature/YourFeature)
- Create a new Pull Request

---

## License

This project is licensed under the GNU General Public License (GPL) version 3 - see the [LICENSE file](LICENSE) for
details.

---

## Authors

- Roberto Manfreda

---

## Repository

The source code for JCGE can be found at:
[GitHub Repository](https://github.com/robertomanfreda/java-code-generation-engine)]
