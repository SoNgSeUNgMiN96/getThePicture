#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("detection");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("detection")
//      }
//    }
extern "C"
JNIEXPORT void  JNICALL
Java_org_tensorflow_lite_examples_detection_Process_ConvertRGBtoGray(JNIEnv *env, jclass clazz,
                                                                     jlong mat_addrinput,
                                                                     jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoGray()

    String s="";

    Mat &orinImage1 = *(Mat *) mat_addrinput;
    Mat &orinImage2 = *(Mat *) mat_addr_result;

    Mat srcImage1, srcImage2;

    cvtColor(orinImage1, srcImage1, COLOR_RGB2GRAY);
    cvtColor(orinImage2, srcImage2, COLOR_RGB2GRAY);


    vector<KeyPoint> keypoints1, keypoints2;
    Mat descriptors1, descriptors2;


    Ptr<ORB> orb = ORB::create(1000);

    orb->detectAndCompute(srcImage1, noArray(),keypoints1,descriptors1);
    orb->detectAndCompute(srcImage2, noArray(), keypoints2, descriptors2);

    //Ptr<BRISK> brisk = BRISK::create();
    //brisk->detectAndCompute(srcImage1,noArray(),keypoints1,descriptors1);
    //brisk->detectAndCompute(srcImage2,noArray(),keypoints2,descriptors2);


    vector<DMatch> matches;
    //BFMatcher matcher(NORM_HAMMING);
    //matcher.match(descriptors1,descriptors2,matches);

    s +="matches.size() = "+to_string(matches.size())+"\n";

    double minDist, maxDist;
    minDist = maxDist = matches[0].distance;

    for (int i = 1; i < matches.size(); i++) {
        double dist = matches[i].distance;
        if(dist<minDist) minDist = dist;
        if(dist>maxDist) maxDist = maxDist;
    }

    s += "minDist = "+to_string(minDist)+" maxDist = "+to_string(maxDist)+"\n";

    vector<DMatch> goodMatches;
    double fTh = (minDist+maxDist)/2;

    for (int i = 0; i < matches.size(); i++) {
        if(matches[i].distance <= fTh)
            goodMatches.push_back(matches[i]);
    }


    s += "gootMatches.size() = "+to_string(goodMatches.size())+"\n";

    if(minDist<90&&goodMatches.size()>4){
        s += "match!!";
    }else s+="matchFail";


    //return env->NewStringUTF(s.c_str());
    //return Result;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_org_tensorflow_lite_examples_detection_ImageActivity_orbFeatureJNI2(JNIEnv *env, jobject thiz,
                                                                         jlong input_image,
                                                                         jlong output_image) {
    // TODO: implement orbFeatureJNI2()
    String s="";

    Mat &orinImage1 = *(Mat *) input_image;
    Mat &orinImage2 = *(Mat *) output_image;

    Mat srcImage1, srcImage2;

    cvtColor(orinImage1, srcImage1, COLOR_RGB2GRAY);
    cvtColor(orinImage2, srcImage2, COLOR_RGB2GRAY);


    vector<KeyPoint> keypoints1, keypoints2;
    Mat descriptors1, descriptors2;
    vector<DMatch> matches;


    Ptr<ORB> orb = ORB::create(1000);


    orb->detectAndCompute(srcImage1, noArray(),keypoints1,descriptors1);
    orb->detectAndCompute(srcImage2, noArray(), keypoints2, descriptors2);

    BFMatcher matcher(NORM_HAMMING);

    matcher.match(descriptors1,descriptors2,matches);






    double minDist, maxDist;
    minDist = maxDist = matches[0].distance;

    for (int i = 1; i < matches.size(); i++) {
        double dist = matches[i].distance;
        if(dist<minDist) minDist = dist;
        if(dist>maxDist) maxDist = maxDist;
    }

    s += "minDist = "+to_string(minDist)+"\n";

    vector<DMatch> goodMatches;
    double fTh = minDist+20;
    double sum =0;

    for (int i = 0; i < matches.size(); i++) {
        if(matches[i].distance <= fTh){
            goodMatches.push_back(matches[i]);
            sum += matches[i].distance;
        }
    }

    s += "avr = "+to_string((sum/goodMatches.size()))+"\n";
    s += "gootMatches.size() = "+to_string(goodMatches.size())+"\n";


    if(minDist<90&&goodMatches.size()>4){
        s += "match!!\n";
    }else s+="matchFail\n";



    return env->NewStringUTF(s.c_str());
    //return Result;
}