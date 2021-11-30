import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.sun.jna.WString;

import java.util.Scanner;
public class awsTest {
    /*
     * Cloud Computing, Data Computing Laboratory
     * Department of Computer Science
     * Chungbuk National University
     */
    static AmazonEC2 ec2;
    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-2") /* check the region at AWS console */
                .build();
    }
    public static void main(String[] args) throws Exception {
        init();
        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;
        while(true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("                                                            ");
            System.out.println("  Cloud Computing, Computer Science Department              ");
            System.out.println("                           at Chungbuk National University  ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones         ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");
            number =menu.nextInt();

            switch (number) {
                case 1:
                    listInstances();
                    break;
                case 2:
                    AvailableZones();
                    break;
                case 3:
                    StartInstance();
                    break;
                case 4:
                    AvailableRegions();
                    break;
                case 5:
                    StopInstance();
                    break;
                case 6:
                    CreateInstance();
                    break;
                case 7:
                    RebootInstance();
                    break;
                case 8:
                    IistImage();
                    break;
                case 99:
                    System.exit(0);
                    break;

                default:
                    System.out.printf("메뉴에 있는 번호를 정확히 입력해주십시오");
            }

        }
    }
    public static void listInstances()
    {
        System.out.println("Listing instances....");
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                                    "[AMI] %s, " +
                                    "[type] %s, " +
                                    "[state] %10s, " +
                                    "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }
    public static void AvailableZones()
    {

    }
    public static void StartInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        Scanner id_string = new Scanner(System.in);
        System.out.printf("시작할 인스턴트를 id를 적어주세요 : ");
        String instance_id = id_string.nextLine();

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);
    }
    public static void AvailableRegions()
    {

    }
    public static void StopInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        Scanner id_string = new Scanner(System.in);
        System.out.printf("동작을 중지할 인스턴트의 id를 적어주세요  : ");
        String instance_id = id_string.nextLine();

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.stopInstances(request);

    }
    public static void CreateInstance()
    {

    }
    public static void RebootInstance()
    {

    }
    public static void IistImage()
    {

    }
}
