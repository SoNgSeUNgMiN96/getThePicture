#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
using namespace cv;
using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_com_cookandroid_opencvtest_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_cookandroid_opencvtest_ImageActivity_detectEdgeJNI(JNIEnv *env, jobject thiz,
                                                            jlong input_image, jlong output_image,
                                                            jint th1, jint th2) {
    // TODO: implement detectEdgeJNI()
    Mat &inputMat = *(Mat *) input_image;
    Mat &outputMat = *(Mat *) output_image;

    cvtColor(inputMat, outputMat, COLOR_RGB2GRAY);
    Canny(outputMat, outputMat, th1, th2);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cookandroid_opencvtest_ImageActivity_orbFeatureJNI(JNIEnv *env, jobject thiz,
                                                            jlong input_image, jlong output_image) {
    // TODO: implement orbFeatureJNI()

    Mat &inputMat = *(Mat *) input_image;
    Mat &outputMat = *(Mat *) output_image;

    Mat inputMat_gray_image;
    Mat Target_gray_image;

    vector<cv::KeyPoint> TargetKeypoints, ReferenceKeypoints;
    Mat TargetDescriptor, ReferDescriptor;

    cvtColor(inputMat, inputMat_gray_image, COLOR_RGB2GRAY);
    cvtColor(outputMat, Target_gray_image, COLOR_RGB2GRAY);

    Ptr<Feature2D> orb = ORB::create(10);

    Ptr<DescriptorMatcher> Matcher_ORB = BFMatcher::create(NORM_HAMMING);		// Brute-Force matcher create method

    vector<DMatch> matches;	// Class for matching keypoint descriptors.

    orb->detectAndCompute(inputMat_gray_image, Mat(),ReferenceKeypoints,ReferDescriptor);
    orb->detectAndCompute(Target_gray_image, Mat(), TargetKeypoints, TargetDescriptor);// detects keypoints and computes the descriptors


    Matcher_ORB->match(TargetDescriptor, ReferDescriptor, matches);	// Find the best match for each descriptor from a query set.
    sort(matches.begin(), matches.end());
    const int match_size = matches.size();
    vector<cv::DMatch> good_matches(matches.begin(), matches.begin() + (int)(match_size * 0.5f));
    Mat Result;
    drawMatches(Target_gray_image, TargetKeypoints, inputMat_gray_image, ReferenceKeypoints, matches, Result, Scalar::all(-1), Scalar(-1), vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS);






    string a = to_string(matches.size());
    return env->NewStringUTF(a.c_str());
    //return Result;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_cookandroid_opencvtest_ImageActivity_orbFeatureJNI2(JNIEnv *env, jobject thiz,
                                                            jlong input_image, jlong output_image) {
    // TODO: implement orbFeatureJNI()
    String s="";

    Mat &orinImage1 = *(Mat *) input_image;
    Mat &orinImage2 = *(Mat *) output_image;

    Mat srcImage1, srcImage2;

    cvtColor(orinImage1, srcImage1, COLOR_RGB2GRAY);
    cvtColor(orinImage2, srcImage2, COLOR_RGB2GRAY);


    vector<KeyPoint> keypoints1, keypoints2;
    Mat descriptors1, descriptors2;


    Ptr<ORB> orb = ORB::create(1000);

    orb->detectAndCompute(srcImage1, noArray(),keypoints1,descriptors1);
    orb->detectAndCompute(srcImage2, noArray(), keypoints2, descriptors2);

    Ptr<BRISK> brisk = BRISK::create();
    brisk->detectAndCompute(srcImage1,noArray(),keypoints1,descriptors1);
    brisk->detectAndCompute(srcImage2,noArray(),keypoints2,descriptors2);


    vector<DMatch> matches;
    BFMatcher matcher(NORM_HAMMING);
    matcher.match(descriptors1,descriptors2,matches);

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


    return env->NewStringUTF(s.c_str());
    //return Result;
}