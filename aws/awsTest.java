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
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";

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
            System.out.println("  9. terminate instance          10. search instance with status  ");
            System.out.println(" 11. start all instance          12. stop all instance  ");
            System.out.println(" 13. list security groups        14. list key pair  ");
            System.out.println(ANSI_CYAN +"\n                                 99. quit " + ANSI_RESET);
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");
            number =menu.nextInt();

            switch (number) {
                /* 기본 메뉴 구현 */
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
                    IistImages();
                    break;
                /* 추가 메뉴 구현 */
                case 9:
                    TerminateInstance();
                    break;
                case 10:
                    SearchInstancewithStatus();
                    break;
                case 11:
                    StartAllInstance();
                    break;
                case 12:
                    StopAllInstance();
                    break;
                case 13:
                    listSecurityGroups();
                    break;
                case 14:
                    listKeyPairs();
                    break;
                case 99:
                    System.exit(0);
                    break;

                default:
                    System.out.printf("메뉴에 있는 번호를 정확히 입력해주십시오");
            }

        }
    }

    /********************** 기본 메뉴 구현 **********************/

    /* 메뉴 1 : 인스턴스 목록 출력 */
    public static void listInstances()
    {
        int index = 0;
        System.out.println("Listing instances....");
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.print(index++ + ". ");
                    System.out.printf(
                            "[id] %s, " +
                                    "[AMI] %s, " +
                                    "[type] %s, " +
                                    "[state] ",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType());
                    /* InstanceState 상태 코드 :
                        0 : pending
                        16 : running
                        32 : shutting-down
                        48 : terminated
                        64 : stopping
                        80 : stopped
                    */
                    if(instance.getState().getCode()<=16) {
                        System.out.printf(ANSI_BLUE+"%10s, ", instance.getState().getName()+ANSI_RESET);
                    }
                    else if(instance.getState().getCode()>=64) {
                        System.out.printf(ANSI_YELLOW+"%10s, ", instance.getState().getName()+ANSI_RESET);
                    }
                    else{
                        System.out.printf(ANSI_RED+"%10s, ", instance.getState().getName()+ANSI_RESET);
                    }
                    System.out.printf(
                            "[monitoring state] %s", instance.getMonitoring().getState());
                }
                System.out.println();
            }
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }

    /* 메뉴 2 : Available zone 목록 출력 */
    public static void AvailableZones()
    {
        int index = 0;
        System.out.println("Available zones . . .");
        DescribeAvailabilityZonesResult zones_response =
                ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.print(index++ + ". ");
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

    /* 메뉴 3 : 특정 인스턴스를 시작하기 */
    public static void StartInstance()
    {
        Scanner id_string = new Scanner(System.in);

        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        System.out.println("Starting " + instance_id + " . . .");

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);
        System.out.println("Successfully started instance "+ request.getInstanceIds());
    }

    /* 메뉴 4 : Available region 목록 출력 */
    public static void AvailableRegions()
    {
        int index = 0;
        System.out.println("Available Regions . . .");
        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.print(index++ + ". ");
            System.out.printf(
                    "[region] %s, " +
                            "[endpoint] %s",
                    region.getRegionName(),
                    region.getEndpoint());
            System.out.println();
        }
    }

    /* 메뉴 5 : 특정 인스턴스를 중지하기  */
    public static void StopInstance()
    {
        Scanner id_string = new Scanner(System.in);
        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        System.out.println("Stopping " + instance_id + " . . .");

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.stopInstances(request);
        System.out.println("Successfully stop instance "+ request.getInstanceIds());

    }

    /* 메뉴 6 : AMI ID를 통해 인스턴스를 생성하기  */
    public static void CreateInstance()
    {
        //기능 추가 : 인스턴스 이름도 함께 request하기
        Scanner name_string = new Scanner(System.in);
        System.out.printf("생성할 인스턴스 이름을 입력하시오 : ");
        String new_instance_name = name_string.nextLine();

        Scanner ami_string = new Scanner(System.in);
        System.out.printf("이미지 ID를 입력하시오 : ");
        String ami_id = ami_string.nextLine();

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

        Tag tag = new Tag()
                .withKey("Name")
                .withValue(new_instance_name);

        CreateTagsRequest tag_request = new CreateTagsRequest()
                .withResources(reservation_id)
                .withTags(tag);

        ec2.createTags(tag_request);

        System.out.println(
                "Successfully started EC2 instance [" +new_instance_name +"] "+ reservation_id +
                        " based on AMI " + ami_id);

    }

    /* 메뉴 7 : 특정 인스턴스를 재부팅하기  */
    public static void RebootInstance()
    {
        Scanner id_string = new Scanner(System.in);
        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        System.out.println("Rebooting " + instance_id + " . . .");

        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.rebootInstances(request);
        System.out.println("Succcessfully rebooted instance "+ request.getInstanceIds());
    }

    /* 메뉴 8 : AMI 목록 출력하기  */
    public static void IistImages()
    {
        int index = 0;
        System.out.println("Listing image....");
        Filter filter = new Filter().withName("is-public").withValues("false");
        DescribeImagesRequest request = new DescribeImagesRequest().withFilters(filter);
        DescribeImagesResult result = ec2.describeImages(request);
        for(Image image : result.getImages()) {
            System.out.print(index++ + ". ");
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

    /********************** 추가 메뉴 구현 **********************/

    /* 메뉴 9 : 특정 인스턴스를 종료하기  */
    public static void TerminateInstance()
    {
        Scanner id_string = new Scanner(System.in);
        System.out.printf("인스턴스 ID를 입력하시오 : ");
        String instance_id = id_string.nextLine();

        System.out.println("Terminating " + instance_id + " . . .");

        TerminateInstancesRequest request = new TerminateInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.terminateInstances(request);
        System.out.println("Successfully Terminated instance "+ request.getInstanceIds());
    }

    /* 메뉴 10 : 특정 상태의 인스턴스 목록만 출력하기  */
    public static void SearchInstancewithStatus()
    {
        int menu = 0;
        int index = 0;
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
                System.out.print(index++ + ". ");
                System.out.printf(
                        "[id] %s, " +
                                "[AMI] %s, " +
                                "[type] %s, " +
                                "[state] ",
                        instance.getInstanceId(),
                        instance.getImageId(),
                        instance.getInstanceType());
                if(instance.getState().getCode()<=16) {
                    System.out.printf(ANSI_BLUE+"%10s, ", instance.getState().getName()+ANSI_RESET);
                }
                else if(instance.getState().getCode()>=64) {
                    System.out.printf(ANSI_YELLOW+"%10s, ", instance.getState().getName()+ANSI_RESET);
                }
                else{
                    System.out.printf(ANSI_RED+"%10s, ", instance.getState().getName()+ANSI_RESET);
                }
                System.out.printf(
                        "[monitoring state] %s", instance.getMonitoring().getState());
            }
            System.out.println();
        }
    }

    /* 메뉴 11,12 : 모든 인스턴스 시작/중지하기 */

    /* InstanceState 상태 코드 :
    0 : pending
    16 : running
    32 : shutting-down
    48 : terminated
    64 : stopping
    80 : stopped
    */
    public static void StartAllInstance()
    {
        System.out.println("Starting all instances....");
        boolean done = false;
        DescribeInstancesRequest describe_request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(describe_request);
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    if (instance.getState().getCode() == 80) {  //인스턴스가 stopped 상태 일 때
                        StartInstancesRequest start_Request = new StartInstancesRequest()
                                .withInstanceIds(instance.getInstanceId());
                        ec2.startInstances(start_Request);
                        System.out.println("Starting instance "+ start_Request.getInstanceIds() + " . . .");
                    }
                }
            }
            describe_request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }
        System.out.println("Successfully start all instances :)");
    }

    public static void StopAllInstance()
    {
        System.out.println("Stopping all instances....");
        boolean done = false;
        DescribeInstancesRequest describe_request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(describe_request);
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    if (instance.getState().getCode() == 16) {  //인스턴스가 running 상태 일 때
                        StopInstancesRequest stop_Request = new StopInstancesRequest()
                                .withInstanceIds(instance.getInstanceId());
                        ec2.stopInstances(stop_Request);
                        System.out.println("Stopping instance "+ stop_Request.getInstanceIds() + " . . .");
                    }
                }
            }
            describe_request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }
        System.out.println("Successfully stop all instances :)");
    }

    /* 메뉴 13 : 보안그룹 목록 출력하기  */
    public static void listSecurityGroups()
    {

        int index=0;
        System.out.println("Listing Security Groups....\n");

        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();

        DescribeSecurityGroupsResult response =
                ec2.describeSecurityGroups(request);

        for(SecurityGroup group : response.getSecurityGroups()) {
            System.out.print(index++ + ". ");
            System.out.printf(
                    "[name] %-17s " +
                            "[id] %-20s " +
                            "[vpc id] %s " +
                            "[IpPermissions] %d개 " +
                            "[description] %s\n",
                    group.getGroupName(),
                    group.getGroupId(),
                    group.getVpcId(),
                    group.getIpPermissions().size(),
                    group.getDescription()
            );
        }
    }

    /* 메뉴 14 : 키페어 목록 출력하기  */
    public static void listKeyPairs()
    {
        int index=0;
        System.out.println("Listing Key Pairs....\n");

        DescribeKeyPairsResult response =
                ec2.describeKeyPairs();

        for(KeyPairInfo key_pair : response.getKeyPairs()) {
            System.out.print(index++ + ". ");
            System.out.printf(
                    "[name]:%-10s  " +
                            "[fingerprint]: %s",
                    key_pair.getKeyName(),
                    key_pair.getKeyFingerprint());
            System.out.println();
        }
    }
}
