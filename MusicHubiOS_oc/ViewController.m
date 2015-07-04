//
//  ViewController.m
//  MusicHubiOS_oc
//
//  Created by Minhaeng Lee on 6/24/15.
//  Copyright (c) 2015 Minhaeng Lee. All rights reserved.
//

#import "ViewController.h"

#import <OpenAl/al.h>
#import <OpenAl/alc.h>
#include <AudioToolbox/AudioToolbox.h>

@interface ViewController ()

@end

@implementation ViewController

static ALCdevice *openALDevice;
static ALCcontext *openALContext;

- (id)init
{
    self = [super init];
    if (self)
    {
        openALDevice = alcOpenDevice(NULL);
        openALContext = alcCreateContext(openALDevice, NULL);
        alcMakeContextCurrent(openALContext);
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)connectAction:(id *)sender {
    NSLog(@"hello");
    [self playSound];
}

- (void) playSound
{
    NSLog(@"Play!!");
    
    openALDevice = alcOpenDevice(NULL);
    openALContext = alcCreateContext(openALDevice, NULL);
    alcMakeContextCurrent(openALContext);
    
    
    NSUInteger sourceID;
    
    alGenSources(1, &sourceID);
    alSourcef(sourceID, AL_PITCH, 1.0f);
    alSourcef(sourceID, AL_GAIN, 1.0f);

    
    NSString *audioFilePath = [[NSBundle mainBundle] pathForResource:@"ratherbe" ofType:@"wav"];
    NSLog(audioFilePath);
    NSURL *audioFileURL = [NSURL fileURLWithPath:audioFilePath];
    
    AudioFileID afid;
    OSStatus openAudioFileResult = AudioFileOpenURL((__bridge CFURLRef)audioFileURL, kAudioFileReadPermission, 0, &afid);
    
    if (0 != openAudioFileResult)
    {
        NSLog(@"An error occurred when attempting to open the audio file %@: %ld", audioFilePath, openAudioFileResult);
        return;
    }
    
    UInt64 audioDataByteCount = 0;
    UInt32 propertySize = sizeof(audioDataByteCount);
    OSStatus getSizeResult = AudioFileGetProperty(afid, kAudioFilePropertyAudioDataByteCount, &propertySize, &audioDataByteCount);
    
    if (0 != getSizeResult)
    {
        NSLog(@"An error occurred when attempting to determine the size of audio file %@: %ld", audioFilePath, getSizeResult);
    }
    
    UInt32 bytesRead = (UInt32)audioDataByteCount;
    //bytesRead = 100000;

    //Case 1.
    //통채로 다 플레이 하는 부분
    //bytesRead값이 파일의 전체 바이트 수 : 37530044
    //[self playSoundAll:bytesRead audioId:afid souceId:sourceID];
    
    //Case 2.
    //부분부분 바이트 단위로 잘라서 컨트롤 하고 싶음.
    [self playSoundIterative:100000 audioId:afid souceId:sourceID];
    
    
    NSLog(@"End!");
}

-(void) playSoundAll:(UInt32) bytesRead
             audioId:(AudioFileID) afid
             souceId:(NSUInteger) sourceID
{
    NSLog(@"bytesRead : %d", bytesRead);
    void *audioData = malloc(bytesRead);
    //바이트 불러오고
    OSStatus readBytesResult = AudioFileReadBytes(afid, false, 0, &bytesRead, audioData);
    
    if (0 != readBytesResult)
    {
        //NSLog(@"An error occurred when attempting to read data from audio file %@: %ld", audioFilePath, readBytesResult);
    }
    
    //오디오 파일 닫고
    AudioFileClose(afid);
    
    //Buffer 초기화, 이 버퍼는 openAL 에서 지원하는 것으로 추정
    ALuint outputBuffer;
    alGenBuffers(1, &outputBuffer);
    
    //Buffer에 쓰기,
    //from audioData to outputBuffer
    alBufferData(outputBuffer, AL_FORMAT_STEREO16, audioData, bytesRead, 44100);
    //audioData 닫아줌 더이상 안쓰니깐...
    if (audioData)
    {
        free(audioData);
        audioData = NULL;
    }
    //사운드 소스:스피커 에 버퍼 집어넣음
    alSourcei(sourceID, AL_BUFFER, outputBuffer);
    //플레이
    alSourcePlay(sourceID);
}

-(void) playSoundIterative:(UInt32) bytesRead
             audioId:(AudioFileID) afid
             souceId:(NSUInteger) sourceID
{
    NSLog(@"bytesRead : %d", bytesRead);
    void *audioData = malloc(bytesRead);
    OSStatus readBytesResult = AudioFileReadBytes(afid, false, 0, &bytesRead, audioData);

    if (0 != readBytesResult)
    {
       // NSLog(@"An error occurred when attempting to read data from audio file %@: %ld", audioFilePath, readBytesResult);
    }

    //1. 같은 내용으로 여러번 반복 가능한가?
    //2. 다른 내용 : 쪼개진 여러개의 바이트어래이를 붙여서 하나의 블럭처럼 플레이 가능한가?
    //3. ....
    
    AudioFileClose(afid);
    //Output buffer초기화, 한번만 하면 되지 않을까...
    //어차피 사이즈 일정한데..
    ALuint outputBuffer;
    alGenBuffers(1, &outputBuffer);
    
    //n번 반복하고 싶음!
    for(int i = 0 ; i < 10 ; i ++){
        NSLog(@"i:%d", i);

        //데이터 버퍼에 쓰기
        alBufferData(outputBuffer, AL_FORMAT_STEREO16, audioData, bytesRead, 44100);

        alSourcei(sourceID, AL_BUFFER, outputBuffer);
        alSourcePlay(sourceID);
    }

}


@end
