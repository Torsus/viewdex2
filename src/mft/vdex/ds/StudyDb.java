
/*
 * StudyDb.java
 *
 * Created on den 19 juni 2007, 15:18
 *
 */
package mft.vdex.ds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.media.jai.PlanarImage;
import mft.vdex.app.AppMainAdmin;
import mft.vdex.imageio.*;
//import mft.vdex.imageio.ImageLoaderDICOM;
//import org.dcm4che.data.Dataset;
//import org.dcm4che.dict.Tags;

/**
 *
 * @author sune
 */
public class StudyDb {

    private AppMainAdmin appMainAdmin;
    private File studyImageDbRoot;
    private String studyImageDbRoot_mod;
    private File studySelectedImageDbRoot;
    private List studyList;
    private String[] userProp;
    private Properties props;
    private int[] studySelectedStatus = new int[4];

    protected String filterExtension[] = {".dcm", ".IMA"};
    //protected String filterExtension[] = {""};
    protected String[][] fileInfoList = new String[1][3];

    private String studyName;
    public ArrayList<StudyDbStackNode> zeroNodeList = new ArrayList<StudyDbStackNode>();
    public ArrayList<StudyDbStackNode> zeroNodeAsStackList = new ArrayList<StudyDbStackNode>();
    public ArrayList<StudyDbStackNode> stackNodeList = new ArrayList<StudyDbStackNode>();
    public ArrayList<StudyDbStackNode> rootNodeList = new ArrayList<StudyDbStackNode>();
    public ArrayList<StudyDbStackNode> rootNodeListMaster = new ArrayList<StudyDbStackNode>();

    // ProgressBar status
    public int pbTotalCnt;
    public int pbCurrentCnt;

    // multi-frame
    public ArrayList<StudyDbStackNode> zeroMfNodeList = new ArrayList<StudyDbStackNode>();
    public ArrayList<StudyDbStackNode> zeroMfNodeAsStackList = new ArrayList<StudyDbStackNode>();

    public StudyDb(AppMainAdmin appMainAdmin, String studyname) {
        this.appMainAdmin = appMainAdmin;
        this.studyName = studyname;
    }

    /**
     * Create the zero- and stackNodeList.
     */
    public void createStudyNodeList(String imageDbPath) {
        zeroNodeList = createZeroNodeList(imageDbPath);
        //createZeroNodeListNEW(imageDbPath);
        stackNodeList = createStackNodeList(imageDbPath);
        //rootNodeList.addAll(zeroNodeList);
        //rootNodeList.addAll(stackNodeList);
        //setStackNodeItemCnt(rootNodeList);
    }

    /**
     * Create a <code>StudyDbStackNode<code/> "zero" node. This node contains a
     * <code>StudyDbImageNode<code/> list. Only images with the extension
     * defined in the <code>filterExtension<code/> field will be added to the
     * list.
     */
    private ArrayList<StudyDbStackNode> createZeroNodeList(String imageDbPath) {
        String curDir = ".";
        File fileList[];
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        File dbPath = new File(imageDbPath);
        try {
            curDir = dbPath.getCanonicalPath();
        } catch (Exception e) {
        }

        fileList = listFiles(curDir);

        // Check for fileList
        //if(fileList == null)
        int itemCnt = 0;
        StudyDbStackNode stackNode = new StudyDbStackNode(itemCnt, dbPath, filterExtension,
                StudyDbNodeType.NODE_TYPE_ROOT, StudyDbStackType.STACK_TYPE_SINGLE_IMAGE);
        ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
        imageList = createFilteredImageNodeList(dbPath, fileList, filterExtension);
        stackNode.setImageNodeList(imageList);
        nodeList.add(stackNode);
        return nodeList;
    }

    private File[] listFiles(String dir) {
        File[] fileList;
        try {
            File direct = new File(dir);
            fileList = direct.listFiles();
            if (fileList == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return fileList;
    }

    /*
     * Create the filtered imageNodeList. Filter on extension.
     */
    private ArrayList<StudyDbImageNode> createFilteredImageNodeList(File dbPath, File fileList[], String filter[]) {
        int j = 0;
        int cnt = 0;
        int numFiles = fileList.length;

        ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();

        for (int i = 0; i < fileList.length; i++) {
            try {
                if (fileList[i].isFile()) {
                    for (int k = 0; k < filter.length; k++) {
                        if (fileList[i].getName().endsWith(filter[k])) {
                            imageList.add(new StudyDbImageNode(fileList[i], cnt++, dbPath, studyName));
                        }
                    }
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return imageList;
    }

    /**
     * Create a <code>StudyDbStackNode<code/> stack node list. This node
     * contains a <code>StudyDbImageNode<code/> list. Only images with the
     * extension defined in the <code>filterExtension<code/> field will be added
     * to the list
     */
    private ArrayList<StudyDbStackNode> createStackNodeList(String imageDbPath) {
        String curDir = ".";
        File fileList[];
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        File dbPath = new File(imageDbPath);
        try {
            curDir = dbPath.getCanonicalPath();
        } catch (Exception e) {
        }

        fileList = listFiles(curDir);
        //if(fileList == null)

        nodeList = createStackNodeList(fileList);

        for (int i = 0; i < nodeList.size(); i++) {
            int nodeType = nodeList.get(i).getNodeType();
            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                File path = nodeList.get(i).getNodePath();
                String nodeName = path.getName();
                nodeList.get(i).setNodeName(nodeName);
                try {
                    curDir = path.getCanonicalPath();
                } catch (Exception e) {
                }
                fileList = listFiles(curDir);
                ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
                imageList = createFilteredImageNodeList(path, fileList, filterExtension);
                nodeList.get(i).setImageNodeList(imageList);
            }
        }
        return nodeList;
    }

    /*
     * Create the <code>StudyDbStackNode<code/> list. A node is the
     * abstraction of a directory that contain a set of images
     * referred to as a "stack". 
     */
    private ArrayList<StudyDbStackNode> createStackNodeList(File fileList[]) {
        int j = 0;
        int numFiles = fileList.length;
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        for (int i = 0; i < fileList.length; i++) {
            try {
                if (fileList[i].isDirectory()) {
                    nodeList.add(new StudyDbStackNode(i, fileList[i], null,
                            StudyDbNodeType.NODE_TYPE_STACK, StudyDbStackType.STACK_TYPE_STACK_IMAGE));
                }
            } catch (Exception e) {
                continue;
            }
        }
        return nodeList;
    }

    /**
     * Create a copy of a <code>StudyDbStackNode</code> root list and store the
     * list in <code>StudyDb.rootNodeList</code> variable.
     */
    public void setRootNodeListAndCopy(ArrayList<StudyDbStackNode> list) {
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        for (int i = 0; i < list.size(); i++) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            ArrayList<StudyDbImageNode> imageList2 = new ArrayList<StudyDbImageNode>();

            StudyDbStackNode item = list.get(i);
            int itemCnt = item.getItemCnt();
            String[] fileExtension = item.getFileExtension();
            File nodePath = item.getNodePath();
            int nodeType = item.getNodeType();
            int stackType = item.getStackType();

            nodeList.add(new StudyDbStackNode(itemCnt, nodePath, fileExtension, nodeType, stackType));

            imageList = item.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode iItem = imageList.get(j);
                File studyPath = iItem.getStudyPath();
                int imageCnt = iItem.getItemCnt();
                File studyImageDbRoot = iItem.getStudyImageDbRoot();
                String studyName = iItem.getStudyName();

                imageList2.add(new StudyDbImageNode(studyPath, imageCnt, studyImageDbRoot, studyName));
            }
            nodeList.get(i).setImageNodeList(imageList2);
        }
        rootNodeList = nodeList;
    }

    /**
     * Create a copy of a <code>StudyDbStackNode</code> list.
     *
     * @return <code>ArrayList<StudyDbStackNode></code> list.
     */
    public ArrayList<StudyDbStackNode> copyStackNodeList(ArrayList<StudyDbStackNode> list) {
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        for (int i = 0; i < list.size(); i++) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            ArrayList<StudyDbImageNode> imageList2 = new ArrayList<StudyDbImageNode>();

            StudyDbStackNode item = list.get(i);
            int itemCnt = item.getItemCnt();
            String[] fileExtension = item.getFileExtension();
            File nodePath = item.getNodePath();
            int nodeType = item.getNodeType();
            int stackType = item.getStackType();
            String nodeName = item.getNodeName();

            nodeList.add(new StudyDbStackNode(itemCnt, nodePath, nodeName, fileExtension, nodeType, stackType));

            imageList = item.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode iItem = imageList.get(j);
                File studyPath = iItem.getStudyPath();
                int cnt = iItem.getItemCnt();
                int imageNo = iItem.getImageNo();
                File studyImageDbRoot = iItem.getStudyImageDbRoot();
                String studyName = iItem.getStudyName();

                imageList2.add(new StudyDbImageNode(studyPath, cnt, imageNo, studyImageDbRoot, studyName));
            }
            nodeList.get(i).setImageNodeList(imageList2);
        }
        return nodeList;
    }

    /*
     * Create the ZeroNodeAsStackList.
     * REPLACED
     */
 /*
    public void createZeroNodeAsStackList() {
        String charSeq = ",";

        if (zeroNodeList == null) {
            return;
        }
        if (zeroNodeList.size() == 0) {
            return;
        }

        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        StudyDbStackNode stackNodeZero = zeroNodeList.get(0);
        ArrayList<StudyDbImageNode> imageNodeList = stackNodeZero.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        File nodePath = stackNodeZero.getNodePath();
        String[] fileExtension = stackNodeZero.getFileExtension();
        int nodeType = stackNodeZero.getNodeType();
        int stackType = stackNodeZero.getStackType();

        int cnt = 0;
        while (iter.hasNext()) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            StudyDbImageNode imageNode = iter.next();
            imageList.add(imageNode);
            setImageNodeItemCnt(imageList);

            // Find the nodeName
            File studyPath = imageNode.getStudyPath();
            String name = studyPath.getName();
            String str3 = name.replace(".", ", ");
            String[] str4 = str3.split(charSeq);
            int len = str4.length;
            String nodeName = "";
            for (int i = 0; i < len - 1; i++) {
                nodeName = nodeName.concat(str4[i]);
            }

            // Find out if image is a multiframe
            String mod = getModality(studyPath);
            int nbFrames = getNumberOfFrames(studyPath);
            int nbImages = getNumberOfImages(studyPath);

            if ((nbImages > 1 || nbFrames > 1)) {
                StudyDbStackNode stackNode = new StudyDbStackNode(cnt, studyPath, nodeName, fileExtension,
                        StudyDbNodeType.NODE_TYPE_STACK, StudyDbStackType.STACK_TYPE_MULTI_FRAME_STACK_IMAGE);
                ArrayList<StudyDbImageNode> imageList2 = createImageNodeForEachStack(stackNode, imageNode);
                stackNode.setImageNodeList(imageList2);
                nodeList.add(stackNode);
            } else {
                int newNodeType = StudyDbNodeType.NODE_TYPE_STACK;
                nodeList.add(new StudyDbStackNode(cnt, nodePath, nodeName, fileExtension, newNodeType, stackType));
                nodeList.get(cnt).setImageNodeList(imageList);
            }
            cnt++;
        }
        zeroNodeAsStackList = nodeList;
    }*/

 /*
     * Create the ZeroNodeAsStackList.
     */
    public void createZeroNodeAsStackList() {
        String charSeq = ",";

        if (zeroNodeList == null) {
            return;
        }
        if (zeroNodeList.size() == 0) {
            return;
        }

        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        StudyDbStackNode stackNodeZero = zeroNodeList.get(0);
        ArrayList<StudyDbImageNode> imageNodeList = stackNodeZero.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        File nodePath = stackNodeZero.getNodePath();
        String[] fileExtension = stackNodeZero.getFileExtension();
        int nodeType = stackNodeZero.getNodeType();
        int stackType = stackNodeZero.getStackType();

        int cnt = 0;
        while (iter.hasNext()) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            StudyDbImageNode imageNode = iter.next();
            imageList.add(imageNode);
            setImageNodeItemCnt(imageList);

            // Find the nodeName
            File studyPath = imageNode.getStudyPath();
            String name = studyPath.getName();
            String str3 = name.replace(".", ", ");
            String[] str4 = str3.split(charSeq);
            int len = str4.length;
            String nodeName = "";
            for (int i = 0; i < len - 1; i++) {
                nodeName = nodeName.concat(str4[i]);
            }

            // New implementation using dcm4che3 (20220212, sune)
            DicomFileAttributeReader dicomFileAttributeReader = new DicomFileAttributeReader();
            DicomFileImageReader dicomFileImageReader = new DicomFileImageReader();
            try {
                dicomFileAttributeReader.readAttributes(studyPath);
                dicomFileImageReader.readFileImageRaster(studyPath, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int numberOfImages = dicomFileImageReader.getNumberOfImages();
            int numberOfFrames = dicomFileAttributeReader.att.getNumberOfFramesInt();
            
            if ((numberOfImages > 1 || numberOfFrames > 1)) {
                StudyDbStackNode stackNode = new StudyDbStackNode(cnt, studyPath, nodeName, fileExtension,
                        StudyDbNodeType.NODE_TYPE_STACK, StudyDbStackType.STACK_TYPE_MULTI_FRAME_STACK_IMAGE);
                ArrayList<StudyDbImageNode> imageList2 = createImageNodeForEachStack(stackNode, imageNode);
                stackNode.setImageNodeList(imageList2);
                nodeList.add(stackNode);
            } else {
                int newNodeType = StudyDbNodeType.NODE_TYPE_STACK;
                nodeList.add(new StudyDbStackNode(cnt, nodePath, nodeName, fileExtension, newNodeType, stackType));
                nodeList.get(cnt).setImageNodeList(imageList);
            }
            cnt++;
        }
        zeroNodeAsStackList = nodeList;
    }
    /**
     * Create a copy of a <code>StudyDbStackNode</code> stack list and store the
     * list in <code>StudyDb.stackNodeList</code> variable. NOT IN USE
     */
    public void setStackNodeListAndCopy(ArrayList<StudyDbStackNode> list) {
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        for (int i = 0; i < list.size(); i++) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            ArrayList<StudyDbImageNode> imageList2 = new ArrayList<StudyDbImageNode>();

            StudyDbStackNode item = list.get(i);
            int itemCnt = item.getItemCnt();
            String[] fileExtension = item.getFileExtension();
            File nodePath = item.getNodePath();
            int nodeType = item.getNodeType();
            int stackType = item.getStackType();

            nodeList.add(new StudyDbStackNode(itemCnt, nodePath, fileExtension, nodeType, stackType));

            imageList = item.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode iItem = imageList.get(j);
                File studyPath = iItem.getStudyPath();
                int imageCnt = iItem.getItemCnt();
                File studyImageDbRoot = iItem.getStudyImageDbRoot();
                String studyName = iItem.getStudyName();

                imageList2.add(new StudyDbImageNode(studyPath, imageCnt, studyImageDbRoot, studyName));
            }
            nodeList.get(i).setImageNodeList(imageList2);
        }
        stackNodeList = nodeList;
    }

    /**
     * *****************************************************************
     *
     * Sort stackNode
     *
     *****************************************************************
     */
    /*
     * Sort the <code>StudyDbStackNode<code/> list.
     */
    public void sortRootNodeList(String stackSortOrder) {

        if (stackSortOrder.equalsIgnoreCase("random")) {
            if (rootNodeList != null) {
                Collections.shuffle(rootNodeList);
            }
        } else {
            if (stackSortOrder.equalsIgnoreCase("sequence")) {
                if (rootNodeList != null) {
                    boolean valid = stackNodeNameValid();
                    if (valid) {
                        Collections.sort(rootNodeList);
                    }
                }
            }
        }
        setStackNodeItemCnt(rootNodeList);
    }

    /*
     * Find if the <code>StudyDbStackNode<code/> nodeName is valid for sort.
     * @return <code>true</code> if all the nodeNames are parsed as integers.
     * @return <code>false</code> if any of the nodeNames are NOT parsed as integers.
     * 
     * 
     * From the API documentation... Integer.parseInt(str)
     * Parses the string argument as a signed decimal integer. The characters
     * in the string must all be decimal digits, except that the first character
     * may be an ASCII minus sign '-' ('\u002D') to indicate a negative value.
     * The resulting integer value is returned, exactly as if the argument and
     * the radix 10 were given as arguments to the parseInt(java.lang.String, int)
     * method.
     * Throws NumberFormatException - if the string does not contain a parsable
     * integer.
     */
    private boolean stackNodeNameValid() {
        boolean status = true;
        int val;

        Iterator<StudyDbStackNode> iter = rootNodeList.iterator();
        while (iter.hasNext()) {
            StudyDbStackNode stackNode = iter.next();
            String nodeName = stackNode.getNodeName();

            if (nodeName != null) {
                try {
                    //a = str.trim();
                    val = Integer.parseInt(nodeName);
                } catch (NumberFormatException e) {
                    status = false;
                    //System.out.println("StudyDb:stackNodeNameValid: NumberFormatException");
                    System.out.println("StudyDb:stackNodeNameValid: Imagename or stackname not interpreted"
                            + " as an integer value. Sequence sorting is not possible. ");
                    break;
                }
            }
        }
        return status;
    }

    /**
     * Create the rootNodeList.
     */
    public void createRootNodeList(ArrayList<StudyDbStackNode> znList, ArrayList<StudyDbStackNode> snList) {
        rootNodeList = null;
        rootNodeList = new ArrayList<StudyDbStackNode>();

        if (znList != null) {
            rootNodeList.addAll(znList);
        }
        if (snList != null) {
            rootNodeList.addAll(snList);
        }

        setStackNodeItemCnt(rootNodeList);
    }

    /**
     * **************************************************************
     *
     * Sort zeroNodeList
     *
     ***************************************************************
     */
    /*
     * Sort the <code>StudyDbImageNode<code/> (zeroNode) imageNode list.
     * The list is only sorted for 'nodeType == NODE_TYPE_ROOT'.
     * The <code>StudyDbStackNode<code/> node list is not sorted
     */
    public void sortZeroNodeList(String imageSortOrder) {
        if (imageSortOrder.equalsIgnoreCase("random")) {
            for (int i = 0; i < zeroNodeList.size(); i++) {
                ArrayList<StudyDbImageNode> list = zeroNodeList.get(i).getImageNodeList();
                Collections.shuffle(list);
            }
        } else if (imageSortOrder.equalsIgnoreCase("sequence")) {
            for (int i = 0; i < zeroNodeList.size(); i++) {
                ArrayList<StudyDbImageNode> list = zeroNodeList.get(i).getImageNodeList();
                Collections.sort(list);
            }
        }
    }

    /**
     * *****************************************************************
     *
     * Sort imageNode
     *
     *****************************************************************
     */
    /*
     * Custom sort.
     * zzzz
     */
    public void sortStackNodeImageNodeListCustom() {

        // status for the progressBar dialog
        //pbTotalCnt = stackNodeList.size();
        for (int i = 0; i < stackNodeList.size(); i++) {
            //pbCurrentCnt = i;
            StudyDbStackNode stackNode = stackNodeList.get(i);
            File path = stackNode.getNodePath();
            //X appMainAdmin.viewDex.setAppTitle("      Please wait...  Initializing imagestack " + Integer.toString(i) + ",  " + path);
            int nodeType = stackNode.getNodeType();

            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                ArrayList<StudyDbImageNode> list = stackNodeList.get(i).getImageNodeList();
                readImageListCustom(list);
                Collections.sort(list);
                setImageNodeItemCnt(list);
            }
        }
    }

    /*
    * Read the filename and set a new InstanceNumber.
     */
    private void readImageListCustom(ArrayList<StudyDbImageNode> list) {
        StudyDbImageNode imageNode = null;
        //StudyLoader studyLoader = new StudyLoader();
        int a = 0;
        for (int i = 0; i < list.size(); i++) {
            imageNode = list.get(i);
            File filePath = imageNode.getStudyPath();
            //System.out.println("filePath =" + filePath);
            String str = filePath.getName();
            int length = str.length();
            if (str.startsWith("IMG")) {
                String s = str.substring(3, length - 4);
                int num = Integer.parseInt(s) + 100000;
                //System.out.println(num);
                imageNode.setInstanceNumber(num);
            } else {
                if (str.startsWith("IM") && str.charAt(2) != 'G') {
                    String s = str.substring(2, length - 4);
                    int num2 = Integer.parseInt(s);
                    //System.out.println(num2);
                    imageNode.setInstanceNumber(num2);
                }
            }
        }
    }

    /**
     * Sort the <code>StudyDbImageNode<code/> imageNode lists for images
     * contained in a stack directory. The viewing order for images (imagefiles)
     * contained in a stack is always the "natural" order based on the 'Instans
     * Number' There is no property to change this order.
     */
    public void sortStackNodeImageNodeList() {

        // status for the progressBar dialog
        //pbTotalCnt = stackNodeList.size();
        for (int i = 0; i < stackNodeList.size(); i++) {
            //pbCurrentCnt = i;
            StudyDbStackNode stackNode = stackNodeList.get(i);
            File path = stackNode.getNodePath();
            appMainAdmin.viewDex.setAppTitle("      Please wait...  Initializing imagestack " + Integer.toString(i) + ",  " + path);
            int nodeType = stackNode.getNodeType();

            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                ArrayList<StudyDbImageNode> list = stackNodeList.get(i).getImageNodeList();
                readDICOMImageMetaData(list);
                Collections.sort(list);
                setImageNodeItemCnt(list);
            }
        }
    }

    /**
     * Read the DICOM Attribute InstanceNumber for every
     * <code>StudyDbImageNode<code/>.
     */
    /*
    private void readDICOMImageMetaData_replaced(ArrayList<StudyDbImageNode> list) {
        StudyDbImageNode imageNode = null;
        StudyLoader_old studyLoader = new StudyLoader_old();

        for (int i = 0; i < list.size(); i++) {
            imageNode = list.get(i);
            File filePath = imageNode.getStudyPath();
            //System.out.println("Filepath = " + filePath);

            studyLoader.readDICOMImageMetaData(filePath);
            Dataset dataset = studyLoader.getDataSet();
            // (0020,0013) Instance Number
            // This Attribute was named Image Number in earlier versions
            // of the DICOM standard.
            String inStr = dataset.getString(Tags.InstanceNumber, null);
            int instanceNumber = getIntegerValue(inStr);
            imageNode.setInstanceNumber(instanceNumber);
        }
    }*/
    /**
     * Read the DICOM Attribute InstanceNumber for every
     * <code>StudyDbImageNode<code/>.
     */
    private void readDICOMImageMetaData(ArrayList<StudyDbImageNode> list) {
        StudyDbImageNode imageNode = null;
        DicomFileReader studyLoader = new DicomFileReader();

        for (int i = 0; i < list.size(); i++) {
            imageNode = list.get(i);
            File filePath = imageNode.getStudyPath();
            //System.out.println("Filepath = " + filePath);

            // New implementation using dcm4che3 (20220212, sune)
            DicomFileAttributeReader dicomFileAttributeReader = null;
            DicomFileImageDataReader dicomFileImageDataReader = null;
            try {
                dicomFileAttributeReader = new DicomFileAttributeReader();
                dicomFileAttributeReader.readAttributes(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // (0020,0013) IS Instance Number
            // This Attribute named Image Number in earlier versions.
            // of the DICOM standard.
            int instanceNumber = dicomFileAttributeReader.att.getInstanceNumber();
            imageNode.setInstanceNumber(instanceNumber);
        }
    }

    /**
     * Get the integer value.
     */
    private int getIntegerValue(String str) {
        int val = Integer.MIN_VALUE;

        if (str != null) {
            try {
                val = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("StudyDb:getIntegerValue: NumberFormatException");
            }
        }
        return val;
    }

    /**
     * Sort the <code>StudyDbImageNode<code/> imageNode list for images
     * contained in each stack directory. Sort the list depending of the DICOM
     * Code Value 0020,0013. 0020,0013 Instance Number NOT IN USE
     */
    public ArrayList<StudyDbStackNode> sortStackNodeImageNodeInstanceNumber(ArrayList<StudyDbStackNode> list) {
        ArrayList<StudyDbImageNode> list2 = new ArrayList<StudyDbImageNode>();

        for (int i = 0; i < list.size(); i++) {
            StudyDbStackNode stackNode = list.get(i);
            int nodeType = stackNode.getNodeType();

            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
                //list2 = sortImageNodeListInstanceNumber(imageNodeList);
                stackNode.setImageNodeList(list2);
            }
        }
        return list;
    }

    /**
     * *******************************************************************
     *
     * Sort methods implemented for a Study by Tony Svahn 2008-07-01. NOT IN USE
     *
     * *****************************************************************
     */
    /**
     * Sort the <code>StudyDbImageNode<code/> imageNode list. Implemented for a
     * Study by Tony Svahn. Sort the list depending of the DICOM Code Value
     * 0008,0100 If the DICOM tag 0008,0100 is equal to R-10226 set the image as
     * no 1. If the DICOM tag 0008,0100 is equal to R-10242 set the image as no.
     * 2.
     *
     * 0008,0102 Code Scheme Designator 0008,0104 Code Meaning
     * {medio-lateral-oblique, cranio-caudal} 0008,0100 Code Value {R10226,
     * R-10242} NOT IN USE
     */
    /*
    public void sortImageNodeMLOCC2(int instance) {
        for (int i = 0; i < rootNodeList.size(); i++) {
            StudyDbStackNode stackNode = rootNodeList.get(i);
            int nodeType = stackNode.getNodeType();

            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                ArrayList<StudyDbImageNode> imageNodeList = rootNodeList.get(i).getImageNodeList();
                //studyDbImageNodePrint(imageNodeList);
                ArrayList<StudyDbImageNode> list = sortImageNodeListMLOCC(imageNodeList, instance);
                stackNode.setImageNodeList(list);
                //System.out.println("");
                //studyDbImageNodePrint(imageNodeList);
            }
        }
    }*/
    /**
     * Sort the <code>StudyDbImageNode<code/> imageNode list. Implemented for a
     * Study by Tony Svahn. Sort the list depending of the DICOM Code Value
     * 0008,0100 If instance1, get image if DICOM tag 0008,0100 is equal to
     * R-10226. If instance2, get image if DICOM tag 0008,0100 is equal to
     * R-10242.
     *
     * 0008,0102 Code Scheme Designator 0008,0104 Code Meaning
     * {medio-lateral-oblique, cranio-caudal} 0008,0100 Code Value {R10226,
     * R-10242}
     */
    /*
    public ArrayList<StudyDbStackNode> sortImageNodeMLOCC(int instance, ArrayList<StudyDbStackNode> list) {
        ArrayList<StudyDbImageNode> list2 = new ArrayList<StudyDbImageNode>();

        for (int i = 0; i < list.size(); i++) {
            StudyDbStackNode stackNode = list.get(i);
            int nodeType = stackNode.getNodeType();

            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
                list2 = sortImageNodeListMLOCC(imageNodeList, instance);
                stackNode.setImageNodeList(list2);
            }
        }
        return list;
    }*/
    /**
     * Sort the <code>StudyDbImageNode<code/> list
     */
    /*
    private ArrayList<StudyDbImageNode> sortImageNodeListMLOCC(ArrayList<StudyDbImageNode> list, int instance) {
        String tagStr = "00080100";
        String[] codeValue = new String[1];
        ArrayList<StudyDbImageNode> list2 = new ArrayList<StudyDbImageNode>();

        // init
        if (instance == 0) {
            codeValue[0] = "R-10226";
            //codeValue[1] = "R-10242";
        }
        if (instance == 1) {
            codeValue[0] = "R-10242";
            //codeValue[1] = "R-10226";
        }

        for (int i = 0; i < codeValue.length; i++) {
            StudyDbImageNode imageNode = getListItemMLOCC(list, tagStr, codeValue[i]);
            if (imageNode != null) {
                list2.add(imageNode);
            }
        }
        //studyDbImageNodePrint(list2);
        return list2;
    }*/
    /**
     * Get a <code>StudyDbImageNode<code/> containing a specific DICOM tag.
     *
     * @return <code>StudyDbImageNode<code/>
     */
    /*
    private StudyDbImageNode getListItemMLOCC(ArrayList<StudyDbImageNode> list, String tagStr, String codeValue) {
        StudyDbImageNode imageNode = null;
        String tagValue;
        StudyLoader_old studyLoader = new StudyLoader_old();

        for (int i = 0; i < list.size(); i++) {
            imageNode = list.get(i);
            File filePath = imageNode.getStudyPath();
            //System.out.println("Filepath = " + filePath);

            studyLoader.readDICOMImageMetaData(filePath);
            Dataset dataset = studyLoader.getDataSet();

            Dataset viewCodeItem = dataset.getItem(Tags.ViewCodeSeq);
            tagValue = getDataElementName(viewCodeItem, tagStr);

            if (tagValue != null && tagValue.compareToIgnoreCase(codeValue) == 0) {
                break;
            }
        }
        return imageNode;
    }*/
    /**
     * *******************************************************************
     *
     * Misc
     *
     * *****************************************************************
     */
    /**
     * Get the Data Element Name.
     *
     * @param dataset the class that hold all the Dicom overhead data.
     * @param tagstr the Data Element Tag (gggg,eeee), where gggg equates to the
     * Group Number and eeee equates to the Element Number within that Group.
     * @return the Data element Name.
     */
    /*
    private String getDataElementName(Dataset dataset, String tagstr) {
        String str = "0x" + tagstr;
        int val = 0;
        String tagName = null;

        try {
            val = Integer.decode(str);
        } catch (NumberFormatException e) {
            System.out.println("StudyLoader:getTagValue: NumberFormatException");
        }

        if (dataset != null) {
            tagName = dataset.getString(val, null);
        }

        return tagName;
    }*/
    /**
     * *******************************************************************
     *
     * Set
     *
     * *****************************************************************
     */
    /**
     * Set the <code>StudyDbStackNode<code/> zeroNodeList.
     */
    public void setZeroNodeList(ArrayList<StudyDbStackNode> list) {
        zeroNodeList = list;
    }

    /**
     * Get the <code>StudyDbStackNode<code/> zeroNodeList.
     */
    public ArrayList<StudyDbStackNode> getZeroNodeList() {
        return zeroNodeList;
    }

    /**
     * Get the <code>StudyDbStackNode<code/> zeroNodeAsStackList.
     */
    public ArrayList<StudyDbStackNode> getZeroNodeAsStackList() {
        return zeroNodeAsStackList;
    }

    /**
     * Set the <code>StudyDbStackNode<code/> stackNodeList.
     */
    public void setStackNodeList(ArrayList<StudyDbStackNode> list) {
        stackNodeList = list;
    }

    /**
     * Get the <code>StudyDbStackNode<code/> stackNodeList.
     */
    public ArrayList<StudyDbStackNode> getStackNodeList() {
        return stackNodeList;
    }

    /**
     * Set the <code>StudyDbStackNode<code/> list item cnt.
     */
    private void setStackNodeItemCnt(ArrayList<StudyDbStackNode> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setItemCnt(i);
        }
    }

    /**
     * Set the <code>StudyDbStackNode<code/> rootNodeList.
     */
    public void setRootNodeList(ArrayList<StudyDbStackNode> list) {
        rootNodeList = list;
    }

    /**
     * Get the <code>StudyDbStackNode<code/> rootNodeList.
     */
    public ArrayList<StudyDbStackNode> getRootNodeList() {
        return rootNodeList;
    }

    /**
     * Set the <code>StudyDbImageNode<code/> list item cnt.
     */
    private void setImageNodeItemCnt(ArrayList<StudyDbImageNode> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setItemCnt(i);
        }
    }

    /**
     * ********************************************************************
     ********************************************************************
     */
    /**
     * Get the <code>StudyDbStackNode<code/> stackNodeList.
     *
     * @return a true <code>StudyDbStackNode<code/> copy. NOT IN USE
     */
    public ArrayList<StudyDbStackNode> getStackNodeListFromRootNodeList2(ArrayList<StudyDbStackNode> rootNodeList) {
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        for (int i = 0; i < rootNodeList.size(); i++) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            ArrayList<StudyDbImageNode> imageList2 = new ArrayList<StudyDbImageNode>();

            StudyDbStackNode stackNode = rootNodeList.get(i);
            int itemCnt = stackNode.getItemCnt();
            File nodePath = stackNode.getNodePath();
            String[] fileExtension = stackNode.getFileExtension();
            int nodeType = stackNode.getNodeType();
            int stackType = stackNode.getStackType();

            nodeList.add(new StudyDbStackNode(itemCnt, nodePath, fileExtension, nodeType, stackType));

            imageList = stackNode.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode in = imageList.get(j);
                File studyPath = in.getStudyPath();
                int imageCnt = in.getItemCnt();
                File studyImageDbRoot = in.getStudyImageDbRoot();
                String studyName = in.getStudyName();

                imageList2.add(new StudyDbImageNode(studyPath, imageCnt, studyImageDbRoot, studyName));
            }
            nodeList.get(i).setImageNodeList(imageList2);
        }
        return nodeList;
    }

    /**
     * ********************************************************************
     *
     * Utility
     *
     ********************************************************************
     */
    /**
     * Print
     */
    public void printStudyDbRootNode() {
        StudyDbStackNode stackNode;

        for (int i = 0; i < rootNodeList.size(); i++) {
            stackNode = rootNodeList.get(i);
            String str = "itemCnt = " + stackNode.getItemCnt() + ", "
                    + "nodeType = " + stackNode.getNodeType() + ", "
                    + "nodePath = " + stackNode.getNodePath();
            System.out.println(str);
        }
    }

    /**
     * Print a <code>ArrayList<StudyDbStackNode></code> directory list.
     */
    public void printStudyDbStackNodeDirectory(ArrayList<StudyDbStackNode> list, String str) {
        StudyDbStackNode stackNode;

        System.out.println("Print: " + str);
        for (int i = 0; i < list.size(); i++) {
            stackNode = list.get(i);
            String str2 = "StudyDbStackNode: "
                    + "itemCnt = " + stackNode.getItemCnt() + ", "
                    + "nodeType = " + stackNode.getNodeType() + ", "
                    + "nodePath = " + stackNode.getNodePath() + ", "
                    + "nodeName = " + stackNode.getNodeName() + ", "
                    + "stackType = " + stackNode.getStackType();

            System.out.println(str2);

            /*
            ArrayList<StudyDbImageNodeX> imageList = stackNode.getImageNodeList();
            for(int j=0; j<imageList.size(); j++){
                StudyDbImageNodeX imageNode = imageList.get(j);
                String str3 = "itemCnt = " + imageNode.getItemCnt() + " " +
                        "imageNo = " + imageNode.getImageNo() + " " +
                        "instanceNumber = " + imageNode.getInstanceNumber() + " " +
                        "orgImageAllocated = " + getOrgImageStatus(imageNode) + " " +
                        "studyPath = " + imageNode.getStudyPath();
                 System.out.println(str3);
            }
             */
        }
        System.out.println("");
    }

    /**
     * Print
     */
    public void printStudyDbStackNode(ArrayList<StudyDbStackNode> list, int cnt) {
        StudyDbStackNode stackNode;

        System.out.println("Print: " + cnt);
        for (int i = 0; i < list.size(); i++) {
            stackNode = list.get(i);
            String str = "StudyDbStackNode: "
                    + "itemCnt = " + stackNode.getItemCnt() + ", "
                    + "nodeType = " + stackNode.getNodeType() + ", "
                    + "nodePath = " + stackNode.getNodePath();
            System.out.println(str);

            ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode imageNode = imageList.get(j);
                String str2 = "itemCnt = " + imageNode.getItemCnt() + " "
                        + "studyPath = " + imageNode.getStudyPath();
                System.out.println(str2);
            }
        }
        System.out.println("");
    }

    /**
     * Print a <code>ArrayList<StudyDbStackNode></code> list.
     */
    public void printStudyDbStackNode(ArrayList<StudyDbStackNode> list, String str) {
        StudyDbStackNode stackNode;

        System.out.println("Print: " + str);
        for (int i = 0; i < list.size(); i++) {
            stackNode = list.get(i);
            String str2 = "StudyDbStackNode: "
                    + "itemCnt = " + stackNode.getItemCnt() + ", "
                    + "nodeType = " + stackNode.getNodeType() + ", "
                    + "nodePath = " + stackNode.getNodePath() + ", "
                    + "nodeName = " + stackNode.getNodeName() + ", "
                    + "stackType = " + stackNode.getStackType();

            System.out.println(str2);

            ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode imageNode = imageList.get(j);
                String str3 = "itemCnt = " + imageNode.getItemCnt() + " "
                        + "imageNo = " + imageNode.getImageNo() + " "
                        + "instanceNumber = " + imageNode.getInstanceNumber() + " "
                        + "orgImageAllocated = " + getOrgImageStatus(imageNode) + " "
                        + "studyPath = " + imageNode.getStudyPath();
                System.out.println(str3);
            }
        }
        System.out.println("");
    }

    private String getOrgImageStatus(StudyDbImageNode imageNode) {
        String str = "n/a";

        PlanarImage img = imageNode.getOrgImage();
        if (img != null) {
            str = "yes";
        } else if (img == null) {
            str = "no";
        }
        return str;
    }

    /**
     * Print
     */
    public void printStudyDbStackAndImageNode(int cnt) {
        StudyDbStackNode stackNode;

        System.out.println("Print: " + cnt);

        for (int i = 0; i < stackNodeList.size(); i++) {
            stackNode = stackNodeList.get(i);
            String str = "itemCnt = " + stackNode.getItemCnt() + ", "
                    + "nodeType = " + stackNode.getNodeType() + ", "
                    + "nodePath = " + stackNode.getNodePath();
            System.out.println(str);

            ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
            for (int j = 0; j < imageList.size(); j++) {
                StudyDbImageNode imageNode = imageList.get(j);
                String str2 = "itemCnt = " + imageNode.getItemCnt() + " "
                        + "studyPath = " + imageNode.getStudyPath();
                System.out.println(str2);
            }
        }
        System.out.println("");
    }

    /**
     * Print
     */
    private void studyDbImageNodePrint2(ArrayList<StudyDbImageNode> list) {
        for (int i = 0; i < list.size(); i++) {
            StudyDbImageNode imageNode = list.get(i);
            System.out.println("itemCnt = " + " " + imageNode.getItemCnt()
                    + "studyPath = " + imageNode.getStudyPath());
        }
    }

    /*
     * Get the progress bar total cnt.
     */
    public int getSortStackTotalCnt() {
        return pbTotalCnt;
    }

    /*
     * Get the progress bar current cnt.
     */
    public int getSortStackCurrentCnt() {
        return pbCurrentCnt;
    }

    /**
     * ***************************************************************
     *
     * multiFrame section
     *
     ***************************************************************
     */
    /**
     * Create the Multi-frame NodeList.
     */
    /*
    public void createZeroMultiFrameNodeList(String imageDbPath) {
        zeroMfNodeList = createZeroMfNodeList(imageDbPath);
    }'/

    /**
     * Create a <code>StudyDbStackNode<code/> "zero multi-frame" node. This node
     * contains a <code>StudyDbImageNode<code/> list. Only images with the
     * extension defined in the <code>filterExtension<code/> field will be added
     * to the list. Only images with Image IOD Module containing the MultiFrame
     * module will be added to the list. The following Image IOD contains the
     * MultiFrame module. NM, USMF, SCMFSB, SCMFGB, SCMFGW, SCMFTC, XA, RF,
     * RTIM, VidVLEN, VidVLMC VidVLPH, Oph8Bit, Oph16Bit
     *
     * NM Image IOD Module US Multi-Frame Image IOD Module Multi-frame Grayscale
     * Byte SC Image IOD Module ...
     *
     */
 /*
    private ArrayList<StudyDbStackNode> createZeroMfNodeList(String imageDbPath) {
        String curDir = ".";
        File fileList[];
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        File dbPath = new File(imageDbPath);
        try {
            curDir = dbPath.getCanonicalPath();
        } catch (Exception e) {
        }

        fileList = listFiles(curDir);

        // Check for fileList
        //if(fileList == null)
        int itemCnt = 0;
        StudyDbStackNode stackNode = new StudyDbStackNode(itemCnt, dbPath, filterExtension,
                StudyDbNodeType.NODE_TYPE_ROOT, StudyDbStackType.STACK_TYPE_SINGLE_IMAGE);
        ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
        imageList = createFilteredMultiFrameImageNodeList(dbPath, fileList, filterExtension);
        stackNode.setImageNodeList(imageList);
        nodeList.add(stackNode);
        return nodeList;
    }*/

 /*
     * Create a filtered Multi-frame imageNodeList.
     */
 /*
    private ArrayList<StudyDbImageNode> createFilteredMultiFrameImageNodeList(File dbPath, File fileList[], String filter[]) {
        int j = 0;
        int cnt = 0;
        int numFiles = fileList.length;

        ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();

        for (int i = 0; i < fileList.length; i++) {
            try {
                if (fileList[i].isFile()) {
                    // check for Multi-frame NM, MR ...
                    String mod = getModality(fileList[i]);
                    if (mod.equalsIgnoreCase("NM") || mod.equalsIgnoreCase("MR") || mod.equalsIgnoreCase("XA")) {
                        for (int k = 0; k < filter.length; k++) {
                            if (fileList[i].getName().endsWith(filter[k])) {
                                imageList.add(new StudyDbImageNode(fileList[i], cnt++, dbPath, studyName));
                            }
                        }
                    }
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return imageList;
    }*/

 /*
     * Get modality read from DICOM file.
     */
 /*
    private String getModality(File file) {
        StudyLoader_old studyLoader = new StudyLoader_old();

        //=======================================================
        // test History object
        // long t1 = System.currentTimeMillis();
        // end History object
        //=======================================================
        studyLoader.readDICOMImageMetaData(file);

        //=======================================================
        // test History object
        // long t2 = System.currentTimeMillis() - t1;
        // System.out.println("Time to read dicom dataset: " + (System.currentTimeMillis() - t1));
        // end test History object
        //=======================================================
        Dataset dataset = studyLoader.getDataSet();
        String modality = dataset.getString(Tags.Modality, null);
        return modality;
    }*/
    /**
     * Get number of frames in DICOM file.
     *
     * @param file path to image file
     * @return number of frames
     */
    /*
    private int getNumberOfFrames(File file) {
        int nb = 0;

        StudyLoader_old studyLoader = new StudyLoader_old();
        studyLoader.readDICOMImageMetaData(file);
        Dataset dataset = studyLoader.getDataSet();
        String nbFrames = dataset.getString(Tags.NumberOfFrames, null);
        if (nbFrames != null) {
            nb = Integer.parseInt(nbFrames);
        }
        return nb;
    }*/
    /**
     * Get number of images in DICOM file.
     *
     * @param filePath the path to the image.
     * @return the number of images .
     */
    /*
    private int getNumberOfImages(File filePath) {
        ImageLoaderDICOM imageLoader = new ImageLoaderDICOM();
        int nbImages = imageLoader.getNumberOfImages(filePath);
        return nbImages;
    }*/
    /**
     * Get the <code>StudyDbStackNode<code/> zeroMfNodeList.
     */
    /*
    public ArrayList<StudyDbStackNode> getZeroMfNodeList() {
        return zeroMfNodeList;
    }*/
    /**
     * Get the <code>StudyDbStackNode<code/> zeroMfNodeList.
     */
    /*
    public ArrayList<StudyDbStackNode> getZeroMfNodeAsStackList() {
        return zeroMfNodeAsStackList;
    }*/

 /*
      * Create the ZeroMultiFrameNodeAsMfStackList.
      * For each multiframe imagefile create a stackNode and for all
      * the images create an imageList and add to the stackNode.
      * Add all the stackNode's to a stackNode list. 
     */
 /*
    public void createZeroMultiFrameNodeAsStackList() {
        String charSeq = ",";

        if (zeroMfNodeList == null) {
            return;
        }
        if (zeroMfNodeList.size() == 0) {
            return;
        }

        StudyDbStackNode stackNode = zeroMfNodeList.get(0);
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter2 = imageNodeList.iterator();
        ArrayList<StudyDbStackNode> nodeList = new ArrayList<StudyDbStackNode>();

        int itemCnt = 0;
        while (iter2.hasNext()) {
            // first image in the list
            StudyDbImageNode imageNode = iter2.next();
            File studyPath = imageNode.getStudyPath();
            // create a stackNode for each stack in the image
            StudyDbStackNode stackNode2 = new StudyDbStackNode(++itemCnt, studyPath, filterExtension,
                    StudyDbNodeType.NODE_TYPE_STACK, StudyDbStackType.STACK_TYPE_MULTI_FRAME_STACK_IMAGE);

            ArrayList<StudyDbImageNode> imageList = createImageNodeForEachStack(stackNode2, imageNode);
            stackNode2.setImageNodeList(imageList);
            nodeList.add(stackNode2);
        }
        zeroMfNodeAsStackList = nodeList;
    }*/
    
    /**
     * CreateImageNodeForEachStack
     * @param stackNode
     * @param imageNode
     * @return imageList
     */
    private ArrayList<StudyDbImageNode> createImageNodeForEachStack(StudyDbStackNode stackNode, StudyDbImageNode imageNode) {
        File studyPath = imageNode.getStudyPath();
        File nodePath = stackNode.getNodePath();

        // New implementation using dcm4che3 (20220224, sune)
        DicomFileAttributeReader dicomFileAttributeReader = new DicomFileAttributeReader();
        DicomFileImageReader dicomFileImageReader = new DicomFileImageReader();
        try {
            dicomFileAttributeReader.readAttributes(studyPath);
            dicomFileImageReader.readFileImageRaster(studyPath, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numberOfImages = dicomFileImageReader.getNumberOfImages();
        
        ArrayList<StudyDbImageNode> imageList = new ArrayList<>();

        int cnt = 0;
        int imgNo = 0;
        for (int i = numberOfImages; i > 0; i--) {
            imageList.add(new StudyDbImageNode(studyPath, cnt, imgNo, nodePath, studyName));
            cnt++;
            imgNo++;
        }
        return imageList;
    }

    /*
        StudyDbStackNode stackNode = zeroNodeList.get(0);
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        File nodePath = stackNode.getNodePath();
        String[] fileExtension = stackNode.getFileExtension();
        int nodeType = stackNode.getNodeType();
        int stackType = stackNode.getStackType();
        
        int cnt = 0;
        while (iter.hasNext()) {
            ArrayList<StudyDbImageNode> imageList = new ArrayList<StudyDbImageNode>();
            StudyDbImageNode imageNode = iter.next();
            imageList.add(imageNode);
            setImageNodeItemCnt(imageList);
            
            // Find the nodeNmae 
            File studyPath = imageNode.getStudyPath();
            String name = studyPath.getName();
            String str3 = name.replace(".", ", ");
            String[] str4 = str3.split(charSeq);
            int len = str4.length;
            String nodeName = "";
            for(int i = 0; i < len - 1; i++)
                nodeName = nodeName.concat(str4[i]);
            
            int newNodeType = StudyDbNodeType.NODE_TYPE_STACK;
            nodeList.add(new StudyDbStackNode(cnt, nodePath, nodeName, fileExtension, newNodeType, stackType));
            nodeList.get(cnt).setImageNodeList(imageList);
            cnt++;
        }
        zeroNodeAsStackList = nodeList;
    }*/
}
