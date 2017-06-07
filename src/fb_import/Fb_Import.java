/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fb_import;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.logging.Logger;

/**
 *
 * @author Bek(Grace)
 */
public class Fb_Import {

    ChromeDriver driver;
    ChromeOptions options;
    DesiredCapabilities capabilities;
    JavascriptExecutor js = null;
    List<String> friend_list;
    List<String> profile_image_list = new ArrayList<>();
    
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Fb_Import fb_import = new Fb_Import();
        fb_import.setUp();
        
        fb_import.facebookImportFriendsData("+251922847962","hiface123");
        
    }
    
    public void setUp(){
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        options = new ChromeOptions();
        options.addExtensions(new File("C:\\ultrasurf.crx"));
        capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(capabilities);
        
        driver.manage().timeouts().setScriptTimeout(40, TimeUnit.SECONDS); // maybe you need a different timeout
        
    }
    
    
    public void facebookImportFriendsData( String email, String password) throws Exception
    {
        // Get specific URL
        driver.get("https://www.facebook.com");
        //Create web element for the text box
        WebElement email_Input = driver.findElement(By.name("email"));
        WebElement password_Input = driver.findElement(By.name("pass"));
        WebElement form = driver.findElement(By.id("login_form"));
        
        email_Input.sendKeys(email);
        password_Input.sendKeys(password);
        form.submit();
        
//        driver.get("https://www.facebook.com/bbe.ki.12/friends");
//        
//        //Get total height
//        By selBy = By.tagName("body"); 
//        int initialHeight = driver.findElement(selBy).getSize().getHeight();
//        int currentHeight = 0;
//
//        while(initialHeight != currentHeight){
//                initialHeight = driver.findElement(selBy).getSize().getHeight();
//
//                //Scroll to bottom
//                ((JavascriptExecutor) driver).executeScript("scroll(0," + initialHeight + ");");
//
//                System.out.println("Sleeping... wleepy");
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException ex) {
//            }
//                currentHeight = driver.findElement(selBy).getSize().getHeight();
//        }
//        
//        List<WebElement> friendsLists = driver.findElements(By.className("fsl"));
//        
//        for (WebElement element : friendsLists) {
//            System.out.println(element.getText());
//        }
//        
////        List<WebElement> friendsProfilePhotos = driver.findElements(By.className("_5q6s _8o _8t lfloat _ohe"));
//        List<WebElement> friendsProfilePhotos = driver.findElements(By.cssSelector("._5q6s._8o._8t.lfloat._ohe"));
//        WebElement image = friendsProfilePhotos.get(0).findElement(By.xpath(".//img"));
//        
//        
//        
//        String image_src = image.getAttribute("src");   
//        System.out.println(friendsProfilePhotos.size());
//        System.out.println(friendsLists.size());
//        System.out.println(image_src);
//        
        
        WebElement profile_link = driver.findElement(By.cssSelector("a[title='Profile']"));
        driver.get(profile_link.getAttribute("href"));
        
        StringBuilder add_info = new StringBuilder();
        List<WebElement> additional_info = driver.findElements(By.cssSelector("#u_0_21 ._50f3"));
            additional_info.forEach((webElement) -> {
                add_info.append(",").append(webElement.getText().replaceAll(",",""));
                System.out.println(webElement.getText().replaceAll(",",""));
            });
        
        // click the friends tab
        WebElement friends_tab = driver.findElement(By.cssSelector("a[data-tab-key='friends']"));
        String total_friends = friends_tab.getText();
        driver.get(friends_tab.getAttribute("href"));
        
        // getting the user information
        String user_name = driver.findElement(By.id("fb-timeline-cover-name")).getText();
        StringBuilder personal_info = new StringBuilder();
        personal_info.append("Email,Name,Total Friends,University,School,City,Relationship,Town\n");
        personal_info.append(email).append(",");
        personal_info.append(user_name).append(",");
        personal_info.append(total_friends.replaceAll("\\D+",""));
        personal_info.append(add_info.toString());
        
        File basic_dir = new File("./new_Imported");
        basic_dir.mkdirs();
        File basic_file = new File(basic_dir, "User_Info.csv");
        try (FileWriter file_writer = new FileWriter(basic_file)) {
            file_writer.write(personal_info.toString());
        }
                
//        // scroll until all friends are displayed
//        boolean has_more = true; // don't forget to switch back to true if not true already
//        while (has_more == true) {
//            
//            Thread.sleep(3000);
//            js.executeScript("window.scroll(0, 10000000)");
//            try {
//                driver.findElement(By.cssSelector("img._359.img.async_saving"));
//                js.executeScript("console.log('Passed')");
//                
//            } catch (Exception e) {
//                has_more = false;
//            }
//           
//        } // end-while
        
//        Get total height
        By selBy = By.tagName("body"); 
        int initialHeight = driver.findElement(selBy).getSize().getHeight();
        int currentHeight = 0;

        while(initialHeight != currentHeight){
                initialHeight = driver.findElement(selBy).getSize().getHeight();

                //Scroll to bottom
                ((JavascriptExecutor) driver).executeScript("scroll(0," + initialHeight + ");");

                System.out.println("Sleeping... wleepy");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
            }
                currentHeight = driver.findElement(selBy).getSize().getHeight();
        }
        
        
        // retrieving all friends
        List<WebElement> friends_block = driver.findElements(By.cssSelector("#pagelet_timeline_medley_friends #collection_wrapper_2356318349 ul li"));
        friend_list = new ArrayList<>();
        for (WebElement webElement : friends_block) {
            try {
                String friend_link = webElement.findElement(By.cssSelector("div.fsl.fwb.fcb a")).getAttribute("href");
                String friend_name = webElement.findElement(By.cssSelector("div.fsl.fwb.fcb a")).getText();             
                
                friend_list.add(friend_link);
                
                System.out.println(webElement.findElement(By.cssSelector("div.fsl.fwb.fcb a")).getAttribute("href") );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }            
        }
        
        StringBuilder friend_detail = new StringBuilder();
        for (String friend_link : friend_list) {
            Map<String, String> friend_info = retrieveSingleProfile(friend_link);
            System.out.println("profile link: " + friend_info.get("profile_link"));
            if(friend_info.get("profile_link") instanceof String)
                profile_image_list.add(friend_info.get("profile_link"));
            
            friend_detail.append(friend_info.get("name"));
            friend_detail.append(friend_info.get("work_info"));
            friend_detail.append("\n");            
        }
        
        File friend_dir = new File("./new_Imported");
        basic_dir.mkdirs();
        File friends_file = new File(friend_dir, "friends_list.csv");
        try (FileWriter friend_writer = new FileWriter(friends_file)) {
            friend_writer.write(friend_detail.toString());
        } catch (IOException ex) {
            Logger.getLogger(Fb_Import.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        saveProfileImage();
//        try {    
//            InputStream in = download(image_src);
//            BufferedImage bufImgOne = ImageIO.read(in);
//            ImageIO.write(bufImgOne, "png", new File("test.jpg"));
//            
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
    }
    
    protected Map<String, String> retrieveSingleProfile(String account_link){
        driver.get(account_link);
        Map<String, String> profile_info =  new HashMap<>();   
        
        profile_info.put("name", driver.findElement(By.cssSelector("#fb-timeline-cover-name")).getText());
        profile_info.put("profile_link", driver.findElement(By.cssSelector("img.profilePic.img")).getAttribute("src"));
        
        StringBuilder add_info = new StringBuilder();
        List<WebElement> additional_info = driver.findElements(By.cssSelector("#intro_container_id ul li ._50f3"));
        additional_info.forEach((webElement) -> {
            add_info.append(",").append(webElement.getText().replaceAll(",","") );
        });        
        profile_info.put("work_info", add_info.toString());
        
        return profile_info;
    }
    
    protected boolean saveProfileImage() throws Exception{
        int name = -1;
        for (String friend_link : profile_image_list) {           
//            saveImage(friend_link);
            
            File friend_dir = new File("./new_Imported/Photos");
            friend_dir.mkdirs();
            
            int last_name_Index = friend_link.indexOf(".jpg");
            
            String image_name = friend_link.substring(last_name_Index-7 ,last_name_Index+4);
            try {    
            InputStream in = download(friend_link);
            BufferedImage bufImgOne = ImageIO.read(in);
            ImageIO.write(bufImgOne, "png", new File("./new_Imported/Photos/"+image_name));
            
            } catch (Exception ex) {
                System.out.println("Image download exception ---------"+ex);
            }
        }
        
        return false;
    }
    
    public InputStream download(String url) throws IOException {
        String script = "var url = arguments[0];" +
                "var callback = arguments[arguments.length - 1];" +
                "var xhr = new XMLHttpRequest();" +
                "xhr.open('GET', url, true);" +
                "xhr.responseType = \"arraybuffer\";" + //force the HTTP response, response-type header to be array buffer
                "xhr.onload = function() {" +
                "  var arrayBuffer = xhr.response;" +
                "  var byteArray = new Uint8Array(arrayBuffer);" +
                "  callback(byteArray);" +
                "};" +
                "xhr.send();";
        Object response = ((JavascriptExecutor) driver).executeAsyncScript(script, url);
        // Selenium returns an Array of Long, we need byte[]
        ArrayList<Long> byteList = (ArrayList<Long>) response;
        byte[] bytes = new byte[byteList.size()];
        for(int i = 0; i < byteList.size(); i++) {
            bytes[i] = (byte)(long)byteList.get(i);
        }
        return new ByteArrayInputStream(bytes);
    }
    
    
}
