import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Image;

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
            System.out.println("  9. search instance with status  10.            ");
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
                /* 추가 구현 */
                case 9:
                    SearchInstancewithStatus();
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
        System.out.println("Available zones . . .");
        DescribeAvailabilityZonesResult zones_response =
                ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf(
                    "[id] %s, " +
                            "[region] %s, " +
                            "[zone] %s ",
                    zone.getZoneId(),
                    zone.getRegionName(),
                    zone.getZoneName());
            System.out.println();
        }
    }
    public static void StartInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        Scanner id_string = new Scanner(System.in);

        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        System.out.println("Starting . . . " + instance_id);
        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);
        System.out.println("Succcessfully started instance "+ request.getInstanceIds());
    }
    public static void AvailableRegions()
    {
        System.out.println("Available Regions . . .");
        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.printf(
                    "[region] %s, " +
                            "[endpoint] %s",
                    region.getRegionName(),
                    region.getEndpoint());
            System.out.println();
        }
    }
    public static void StopInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        Scanner id_string = new Scanner(System.in);
        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.stopInstances(request);
        System.out.println("Succcessfully stop instance "+ instance_id);

    }
    public static void CreateInstance()
    {
        Scanner ami_string = new Scanner(System.in);
        System.out.printf("이미지 ID를 입력하시오 : ");
        String ami_id = ami_string.nextLine();

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T1Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();
        System.out.println("Successfully started EC2 instance " +reservation_id +" based on AMI " + ami_id);

    }
    public static void RebootInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        Scanner id_string = new Scanner(System.in);
        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        System.out.println("Rebooting . . . " + instance_id);

        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instance_id);

        RebootInstancesResult response = ec2.rebootInstances(request);
        System.out.println("Succcessfully rebooted instance "+ response);
    }
    public static void IistImage()
    {
        System.out.println("Listing image....");
        Filter filter = new Filter().withName("is-public").withValues("false");
        DescribeImagesRequest request = new DescribeImagesRequest().withFilters(filter);
        DescribeImagesResult result = ec2.describeImages(request);
        for(Image image : result.getImages()) {
            System.out.printf(
                    "[ImageID] %s, " +
                            "[Name] %s, " +
                            "[Owner]  %s",
                    image.getImageId(),
                    image.getName(),
                    image.getOwnerId());
            System.out.println();
        }
    }
    public static void SearchInstancewithStatus()
    {
        int menu = 0;
        Scanner scan_menu = new Scanner(System.in);
        Filter status_filter = new Filter("instance-state-name");

        System.out.printf("검색하고 싶은 인스턴스의 상태를 선택해주세요 ( 1. Running   2. Stopped ) : ");
        while(menu < 1 || menu > 2) {
            while (!scan_menu.hasNextInt()) {
                scan_menu.next();
                System.err.print("숫자를 입력해 주세요.  재 선택 : ");
            }
            menu = scan_menu.nextInt();

            switch (menu) {
                case 1:
                    status_filter.withValues("running");
                    break;
                case 2:
                    status_filter.withValues("stopped");
                    break;
                default:
                    System.err.print("올바른 입력값이 아닙니다.  재 선택 : ");
                    break;
            }
        }

        DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(status_filter);

        DescribeInstancesResult response = ec2.describeInstances(request);
        for (Reservation reservation : response.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                System.out.printf(
                        "[id] %s, " +
                                "[AMI] %s, " +
                                "[type] %s, " +
                                "[state] %s " +
                                "[monitoring state] %s\n",
                        instance.getInstanceId(),
                        instance.getImageId(),
                        instance.getInstanceType(),
                        instance.getState().getName(),
                        instance.getMonitoring().getState());
            }
        }
    }
}
