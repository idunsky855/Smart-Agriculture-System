# Smart Agriculture System - Ambient Invisible Intelligence Project

## Table of Contents

   -   [Overview](#overview)
    -   [Team Members and Roles](#team-members-and-roles)
    -   [Development Methodology](#development-methodology)
    -   [Installation Guide](#installation-guide)
        -   [Docker Installation](#docker-installation)
        -   [Installing IDE (Recommended for Running the Project Comfortably)](#installing-ide-recommended-for-running-the-project-comfortably)
        -   [Installing Android Studio - For Running the Client](#installing-android-studio---for-running-the-client)
    -   [How to Run?](#how-to-run)
        -   [Running the Server](#running-the-server)
        -   [Running the Android Client](#running-the-android-client)
        -   [Python Script](#python-script)
    -   [Notes](#notes)
        -   [General](#general)
        -   [Database Access](#database-access)
          

## Overview 


The Smart Agriculture System is a cutting-edge platform designed to enhance agricultural management by dynamically adjusting growing conditions based on real-time weather and environmental data. It features integrated server-side and client-side components that collaborate to deliver intelligent monitoring and decision-making capabilities.

This system focuses on automating and optimizing plant management through smart control of irrigation, lighting, and environmental factors. By incorporating a range of sensors and actuators, it ensures ideal conditions for plant growth, minimizes the need for manual intervention, and boosts overall efficiency.

## Team Members and Roles
- **Liron Barshishat** - Team Leader / Developer
- **Yaniv Kaveh Shtul** - SCRUM Master / DBA / Developer
- **Avital Shmueli** - Product Owner / Technical Writer / Developer
- **Hadar Zimberg** - System Architect / UI/UX Engineer / Developer 
- **Idan Dunsky** - DevOps / Developer
- **Itzik Levayev** - QA  / Developer

## Development Methodology
This project was developed using the Scrum methodology, structured into five sprints. At the end of each sprint, we prepared a report detailing the completed tasks, challenges encountered, and any pending tasks.

## Installation Guide


### Docker Installation
<div align="center"> <img alt="docker" height="200px" src="https://logos-world.net/wp-content/uploads/2021/02/Docker-Logo.png"> </div>
This project utilizes a Docker image of PostgreSQL, so Docker must be installed on your system. Follow these steps to get started:

1.  Visit the official [Docker website](https://www.docker.com/).
2.  Select the appropriate Docker version for your operating system. For macOS or Linux, hover over the download button to reveal installation options.
3.  Download and run the installer on your computer.
4.  After the installation is complete, Docker will be ready to use.
5.  Ensure Docker is running on your system before starting the project’s server.


### Installing IDE (Recommended for Running the Project Comfortably)
<div align="center"> <img alt="spring boot" height="200px" src="https://spring.io/img/projects/spring-tool.svg"> </div>

1.  **Download Spring Tool Suite (STS):**  
    Get the installer from the official [Spring Tool Suite website](https://spring.io/tools/).
    
2.  **Choose the Appropriate Version:**  
    Select the version that matches your operating system (Windows, macOS, or Linux) and download the installer.
    
3.  **Install Spring Tool Suite:**
    
    -   **For Windows:**
        -   Run the downloaded `.exe` file.
        -   Follow the installation wizard prompts.
        -   Choose the installation directory or proceed with the default.
    -   **For macOS:**
        -   Open the downloaded `.dmg` file.
        -   Drag the STS application into the **Applications** folder.
    -   **For Linux:**
        -   Extract the `.tar.gz` file to your desired directory.
        -   Open a terminal, navigate to the STS directory, and run:
            
            ```bash
            ./STS
            
            ```
            
4.  **Launch Spring Tool Suite:**
    
    -   **Windows:** Open from the Start menu.
    -   **macOS:** Open from the Applications folder.
    -   **Linux:** Launch via terminal or application launcher.
    
    On the first launch, you’ll be prompted to select a workspace directory where your projects will be stored.
    

#### Alternative:

You can also use other Java IDEs with Spring Boot support, such as **IntelliJ IDEA**, **Visual Studio Code**, or **Eclipse**, with the required Spring Boot plugins.

### Installing Android Studio - For running the client 

<div align="center"> <img alt="android studio" height="200px" src="https://developer.android.com/static/studio/images/new-studio-logo-1_1920.png"> </div>

1.  Download **Android Studio (Ladybug)** from the official [site](https://developer.android.com/studio).
    
2.  Choose the appropriate version for your operating system (Windows, macOS, or Linux) and download the installer.
    
3.  Install Android Studio on your computer based on your operating system:
    
    -   **For Windows:**
        -   Run the downloaded `.exe` installer.
        -   Follow the setup wizard to complete the installation.
        -   Choose the installation directory or proceed with the default one.
    -   **For macOS:**
        -   Open the downloaded `.dmg` file.
        -   Drag the Android Studio icon to the **Applications** folder.
    -   **For Linux:**
        -   Extract the downloaded `.tar.gz` file to your desired directory.
        -   Open a terminal, navigate to the extracted directory, and run:
            
            ```bash
            ./studio.sh
            
            ```
            
4.  Launch **Android Studio (Ladybug):**
    
    -   For **Windows:** Open from the Start menu.
    -   For **macOS:** Find it in the Applications folder.
    -   For **Linux:** Use the terminal or application launcher.
5.  On the first run, complete the setup wizard and select a workspace directory where your projects will be stored.
    
#### Alternative:

You can use any other IDE with Android development support, such as **IntelliJ IDEA** or **Visual Studio Code** with the appropriate Android SDK and plugins.


## How to Run?

### Running the Server

1.  **Clone the Repository:**
    
    ```bash
    git clone <repository_url>
    
    ```
    
2.  **Activate Docker:**  
    Ensure Docker is running on your computer as it’s required for the database.
    
3.  **Set Up the Project in Spring Tool Suite (STS):**
    
    -   Right-click on the project in STS.
    -   Select **"Add Gradle Nature"** to configure the project with Gradle.
    
    **_Alternative (Using Terminal):_**  
    Build the project using the following command:
    
    ```bash
    ./gradlew build
    
    ```
    
4.  **Run the Server:**
    
    -   In STS, click the green **Play** button to start the server.
    -   Alternatively, you can run the application from the terminal using:
        
        ```bash
        ./gradlew bootRun
        
        ```

### Running the Android Client

After ensuring the server is up and running (refer to the **Run the Server** section if it’s not), follow these steps to run the Android client:

1.  Navigate to the client folder and open it in **Android Studio (Ladybug version)**.
2.  Create an emulator running **Android 14 (API Level 34 - Target SDK)**.
3.  Refresh Gradle and build the project to ensure all dependencies are properly configured.
4.  In App.java - edit the BASE_URL variable to be the correct IP address.
5.  Click on **Run Project** to launch the Android client.


### Python Script

We’ve provided a Python script that generates plant data and utilizes weather APIs to simulate environmental sensor conditions.

After ensuring the server is up and running (refer to the **Run the Server** section if needed), follow these steps to run the Python script:

1.  **Ensure Python 3 is Installed:**  
    Make sure you have Python 3 installed on your machine. You can verify this by running:
    
    ```bash
    python3 --version
    
    ```
    
2.  **Generate API Keys:**  
    Create new API keys for the following services:
    
    -   [WeatherAPI Documentation](https://www.weatherapi.com/docs/)
    -   [Perenual API Documentation](https://perenual.com/docs/api)
3.  **Navigate to the Script Directory:**  
    Go to the `src/main/scripts/` folder in your project directory:
    
    ```bash
    cd src/main/scripts/
    
    ```
    
4.  **Create the `credentials.json` File:**  
    In the `scripts` folder, create a file named `credentials.json` with the following content:
    
    ```json
    {  
        "weather": {  
            "weather_url": "http://api.weatherapi.com/v1/current.json",  
            "weather_api_key": "<YOUR-API-KEY>"  
        },  
        "plant_images": {  
            "plant_images_url": "https://perenual.com/api/species-list",  
            "plant_images_api_key": "<YOUR-API-KEY>"  
        }  
    }
    
    ```
    
    Replace `<YOUR-API-KEY>` with your actual API keys from WeatherAPI and Perenual.
    
5.  **Install Required Dependencies:**  
    In the terminal, install the `requests` library:
    
    ```bash
    pip install requests
    
    ```
    
6.  **Run the Script:**  
    Execute the script using the following command:
    
    ```bash
    python3 mockup_create_update_plants.py
    
    ```
    

This will create plant data and simulate weather conditions based on real-time API responses.


## Notes

### General

1.  It is recommended to use the client for adding objects, commands, or users to the database.
2.  If you prefer to manually insert objects into the server, you can use the OpenAPI interface available at: [http://localhost:8081/swagger-ui/index.html#/](http://localhost:8081/swagger-ui/index.html#/)


### Database Access

You can access the database through the H2 console at [http://localhost:8085/h2-console](http://localhost:8085/h2-console).

#### Alternative: Access via Shell

To access the database through the shell, follow these steps:

1.  List all running Docker containers:
    
    ```bash
    docker ps
    
    ```
    
    This will display all active containers. Identify the correct container.
    
2.  Access the container's shell:
    
    ```bash
    docker exec -it <container-id> bash
    
    ```
    
3.  Inside the container’s shell, connect to the database:
    
    ```bash
    psql mydatabase -U myuser
    
    ```
    

Congratulations—you’re now connected to the database!
