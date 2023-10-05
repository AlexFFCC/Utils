import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MergePdf {
    public static void main(String[] args) throws IOException {
        List<String> filesPathString = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int number = 1;
        while(number>0){
            System.out.println("輸入PDF檔案路徑");
            String files = scanner.nextLine();
            filesPathString.add(files);
            System.out.println("還有檔案嗎?(Y/N)");
            String continueOrNot = scanner.nextLine();
            if(continueOrNot.equalsIgnoreCase("N")){
                number = -1;
            }else if(continueOrNot.equalsIgnoreCase("Y")){
                number = 1;
            }else{
                System.out.println("錯誤，重來");
                return;
            }
        }
        System.out.println("資料夾存檔路徑");
        String resultFolder = scanner.nextLine();
        List<String> mergeFiles = new ArrayList<>();
        List<PDDocument> pdDocumentList = new ArrayList<>();

        for(String path :filesPathString){
            PDDocument document = null;
            boolean encrypt = false;
            try {
                document = PDDocument.load(new File(path));
            } catch (InvalidPasswordException e) {
                encrypt = true;
            } catch (IOException e){
                e.printStackTrace();
            }

            if (encrypt) {
                System.out.println(path+" <= 此檔案有加密");
                System.out.println("請輸入密碼");
                File file = new File(path);
                String fileName = file.getName();
                String password = scanner.nextLine();
                try {
                    document = PDDocument.load(new File(path),password);
                    AccessPermission accessPermission = new AccessPermission();
                    accessPermission.setCanPrint(true); // 可以打印
                    accessPermission.setCanModify(false); // 不能修改
                    document.setAllSecurityToBeRemoved(true);
                    String decryptFilePath = resultFolder+"\\"+"Decrypt_"+fileName;
                    document.save(decryptFilePath);
                    System.out.println("解密完成並存檔,路徑 => "+decryptFilePath);
                    document.close();
                    pdDocumentList.add(document);
                    mergeFiles.add(decryptFilePath);
                } catch (InvalidPasswordException e) {
                    System.out.println("密碼錯誤");
                    return;
                }catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }else {
                mergeFiles.add(path);
            }
        }

        List<File> fileList = new ArrayList<>();
        for(String pdfPath :mergeFiles){
            File file = new File(pdfPath);
            fileList.add(file);
        }
        File folder = new File(resultFolder);
        if(!folder.exists()){
            folder.mkdir();
        }

        PDFMergerUtility pDFMergerUtility = new PDFMergerUtility();
        pDFMergerUtility.setDestinationFileName(folder+"\\merge.pdf");
        try {
            for(File filePaht:fileList){
                pDFMergerUtility.addSource(filePaht);
            }
            pDFMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            System.out.println("created pdf");
        }catch (Exception e){
            System.out.println(e);
        }
        scanner.close();
    }
}
