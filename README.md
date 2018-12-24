# BASIC INFORMATION

## Project repository
https://github.com/akiutoslahti/cybersecuritybase-project

## Credentials:
Username: admin password: admin12345
Username: user password: user12345

## App business logic:
After user logs in, user is redirected to '/user'. From there, user can follow event registration link to '/form'. After succesfull sign up, user is redirected to '/user', from where user can follow link '/details?query=[name of user]', where user can see his/her own registration details.

After admin logs in, admin is redirected to '/admin'. There admin can see all the details conserning users signed up to event.

# VULNERABILITIES

Most of the vulnerabilities are also indicated in project source code with info on how to fix them. For more info skim through source code and read comments I have written to gain insigth on what makes these vulnerabilities possible. However all vulnerabilities are not intentionally written to source code and are not indicated there.

##Vulnerability 1: A1 - SQL Injection

### SQL injection vulnerability on registration details page

Because controller behind '/details' takes argument from request parameter query and concatenates it to sql expression "select * from signup where name='" + name + "'", SQL injection is possible. To fix this vulnerability raw concatenated sql query should be replaced with JPA repository query where query parameter should be extracted from authentication object.

Steps:
1. Log in to application as user
2. Follow link to register to event
3. Follow link to see registration details
4. Browser should now be on page: http://localhost:8080/details?query=Umberto%20the%20User and here you can execute SQL injection attacks.

Try following:
1. To query all users registered to event: http://localhost:8080/details?query=Umberto%20the%20User%27%20or%20%271%27%20=%20%271
2. To query all usernames and passwords: http://localhost:8080/details?query=Umberto%20the%20User%27%20union%20select%20*%20from%20user_account%20where%20%271%27=%271

## Vulnerability 2: A2 - Broken Authentication

### Permits weak or well-known passwords
New user accounts can be registered on '/register' page. Password field is not sanitized so user can provide weak or blank passwords. To fix, add sanitization to password field to controller handling '/register' page. Sanitization could be e.g. minimum allowed password length and/or complexity requirements.

Steps:
1. Try registering new user account in '/register' with empty password
2. Try to logging in afterwards

### Bruteforce attacks are allowed
Login tries are not logged and monitored, so bruteforce attacks to login are possible. One can for example fuzz login with OWASP ZAP and use common password lists to try several passwords every second. To fix, add logging and monitoring facilities to user login and enforce a timeout after certain number of login tries. Another option could be adding CAPTCHA to user login after certain number of login tries.

Steps:
1. Try fuzzing login with OWASP ZAP or another tools of your choice

### Http session IDs are not invalidated if user does not logout
Http Session ID's have no expiry. If user does not logout, but closes browser, same machine can be used to access same session. Also exposed cookie(e.g. via XSS attack) can be used from another machine to access same user session. To fix, add expriry time to cookies.

Steps:
1. Login as admin account, open developer console and copy JSESSIONID cookie value
2. Use another browser or computer and navigate to login screen
3. Manually add JSESSIONID cookie with previously copied value and navigate again to '/admin'

## Vulnerability 3: A3 - Sensitive Data Exposure

### Http protocol is allowed
Allowing http protocol exposes user request as plain text. To fix, prevent usage of http protocol and enforce use of https protocol with a letsencrypt certificate.

### Passwords in database are not encrypted
Passwords in database are in plain text. To fix, add password encryption e.g. BCrypt to passwords.

Steps:
1. Navigate to '/h2-console', change JDBC URL to jdbc:h2:mem:testdb and connect.
2. Execute query select * from user_account;

## Vulnerability 4: A5 - Broken Access Control

### 'Admin panel' does not check the role of the user
Although only admin users are redirected to '/admin' page, there is no restrictions to access that page. By navigating directly to '/admin' page, every authenticated user can access all details of every registrant. To fix, check users role by fetching username from Authentication object and querying user role from database in controller responsible for '/admin' page. If user has no admin role, do not permit access to '/admin' page.

Steps:
1. Login as user
2. Navigate to url '/admin'

## Vulnerability 5: A6 - Security Misconfiguration

### Weak default admin password
Weak default admin password is used and it can be in fact found from Top 1Million commond passwords list (https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10-million-password-list-top-1000000.txt). To fix, simply change default password to something more secure and enforce password length and complexity rules discussed already in paragraph on vulnerability 2.

Steps:
1. Perform fuzz with OWASP ZAP or tool of your choice and previously given password list.

### Cookie security and httpOnly flags are off
Cookies are not set to secure and http-only modes. Therefore cookies can be extracted e.g. by javascript. To fix, set cookies to secure and http-only modes.

Steps:
1. Login as user
2. Open developer console and execute command alert(document.cookie);

### CSRF protections are disabled
CSRF(cross-site request forgery) protections are disabled, so CSRF attacks are possible. To fix, enable CSRF protection mechanisms built into Spring Boot.

## Vulnerability 6: A7 - Cross-Site Scripting(XSS)

### Possibility to inject javascript in sign up to event form, scripts are executed on pages '/admin' and '/details'
In html templates, every field is set to th:utext. Therefore all javascript fetched from database is executed as e.g. '/admin' or '/details' pages are loaded. Also there are no sanitizations for html/script tags in registration forms. To fix, change every th:utext field from html templates to th:text and add sanitization to form fields in event register controller.

Steps:
1. Login as user, register to event and put <script th:inline="javascript">alert(document.cookie);</script> to one of the form fields.
2. Follow link to see registration details to see XSS vulnerability be exploited.

## Vulnerability 7: A9 - Using Components with Known vulnerabilities.

### Project base is very old and has multiple known vulnerabilities
Project uses Spring Boot 1.4.2 which is already more than two years old and contains several known vulnerabilities. I counted 46 CVE numbers on this project. To fix, simply update to latest Spring Boot version 1.5.18 or consider upgrading to version 2.1.1.

Steps:
1. Try running Maven task dependency-check:check to see all CVE numbers which this old version of Spring Boot and its dependencies have.

## Vulnerability 8: A10 - Insufficient Logging and Monitoring

### No logging that facilitates monitoring
By default Spring Boot does not log nearly anything. It logs something related to system on startup and when errors are encountered but not anything sufficient to detect attacks or other malicious uses are logged. To fix, add spring-boot-starter-logging and configure it in a way that pleases you.
