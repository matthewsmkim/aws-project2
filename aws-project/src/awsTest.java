



import java.util.LinkedList;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;




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
         * credential profile by reading from the credentials file located at credentials
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
        
        while (true) {
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
            int number=menu.nextInt();
            

            switch (number) {
                case 1:
                    listInstances();
                    break;
                case 2:
                    availableZones();
                    break;
                case 3:
                    startInstance();
                    break;
                case 4:
                    availableRegions();
                    break;
                case 5:
                    stopInstance();
                    break;
                case 6:
                    createInstance();
                    break;
                case 7:
                    rebootInstance();
                    break;
                case 8:
                    listImages();
                    break;
                default:
                    break;
                
                    	

            }


        }
    }




    
    // #1
    private static void listInstances() {
        System.out.println("Listing instance....");
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
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
            if (response.getNextToken() == null) {
                done = true;
            }
        }

    }
    
    // #2
    private static void availableZones() {
    	System.out.println("Available zones....");
    	
    	DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

    		for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
    		    System.out.printf("[id] %s " + "[region] %s " + "[zone] %s \n",zone.getZoneId(),zone.getRegionName(),zone.getZoneName());
    		}
    
    }
    
    // #3
    private static void startInstance() {
    	 
    	Scanner in = new Scanner(System.in);
    	String instance_id = in.next();
    	
    	System.out.println("Starting.... %s"+instance_id);
    	
    	DryRunSupportedRequest<StartInstancesRequest> dry_request = () -> {StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);
                
                return request.getDryRunRequest();
                };
                
   
    	
                DryRunResult dry_response = ec2.dryRun(dry_request);

                if(!dry_response.isSuccessful()) {
                    System.out.printf("Failed to start instance %s", instance_id);

                    throw dry_response.getDryRunResponse();
                }

                StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

                ec2.startInstances(request);

                System.out.printf("instance starts successfully - %s", instance_id);

    	       
    }
    
    // #4
    private static void availableRegions() {
    	
    	DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.printf("Found region %s " + "with endpoint %s",region.getRegionName(),region.getEndpoint());
        }
    	
    	
    }
    
    // #5
    private static void stopInstance() {
    	
    	Scanner in = new Scanner(System.in);
    	String instance_id = in.next();
    	
    	DryRunSupportedRequest<StopInstancesRequest> dry_request = () -> { StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

                return request.getDryRunRequest();
            };

            DryRunResult dry_response = ec2.dryRun(dry_request);

            if(!dry_response.isSuccessful()) {
                System.out.printf("Failed to stop instance %s", instance_id);
                throw dry_response.getDryRunResponse();
            }

            StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

            ec2.stopInstances(request);
            System.out.printf("Successfully stop instance %s", instance_id);
    	
    }

    // #6
    private static void createInstance(){
    	
    	Scanner in = new Scanner(System.in);
    	Scanner out = new Scanner(System.in);
        
    	System.out.print("Enter ami id: ");
    	String ami_id = in.next();
    	System.out.print("Enter name: ");
    	String name = out.next();
    	
    	RunInstancesRequest request = new RunInstancesRequest().withImageId(ami_id).withInstanceType(InstanceType.T1Micro).withMaxCount(1).withMinCount(1);
    	RunInstancesResult run_response = ec2.runInstances(request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

        Tag tag = new Tag().withKey("Name").withValue(name);

        CreateTagsRequest tag_request = new CreateTagsRequest().withResources(reservation_id).withTags(tag);
        CreateTagsResult tag_response = ec2.createTags(tag_request);

        System.out.printf("Successfully started EC2 instance %s based on AMI %s",reservation_id, ami_id);

    }
    
    // #7
    private static void rebootInstance(){
    	
    	Scanner in = new Scanner(System.in);
    	String instance_id = in.next();



        RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);

        RebootInstancesResult response = ec2.rebootInstances(request);

        System.out.printf(
            "Reboot is successful - %s", instance_id);
    }

    

    
    
    // #8
    private static void listImages(){
    	System.out.println("Listing images....");
    	
    	DescribeImagesRequest request = new DescribeImagesRequest();
    	
    	while(true) {
    		DescribeImagesResult response= ec2.describeImages(request);
    		for(Image image : response.getImages()) {
    			System.out.printf(
    	    			"[ImageID] %s, " + "[Name] %s, " + "[Owner] %s", image.getImageId(), image.getName(), image.getOwnerId());		
    		}
    		System.out.println();
    	
    	}
    }
    
    
}