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
}

- (void) playSound
{
    NSUInteger sourceID;
    alGenSources(1, &sourceID);
//    
//    NSString *audioFilePath = [[NSBundle mainBundle] pathForResource:@"ting" ofType:@"caf"];
//    NSURL *audioFileURL = [NSURL fileURLWithPath:audioFilePath];
//    
//    AudioFileID afid;
//    OSStatus openAudioFileResult = AudioFileOpenURL((__bridge CFURLRef)audioFileURL, kAudioFileReadPermission, 0, &afid);
//    
//    if (0 != openAudioFileResult)
//    {
//        NSLog(@"An error occurred when attempting to open the audio file %@: %ld", audioFilePath, openAudioFileResult);
//        return;
//    }
//    
//    UInt64 audioDataByteCount = 0;
//    UInt32 propertySize = sizeof(audioDataByteCount);
//    OSStatus getSizeResult = AudioFileGetProperty(afid, kAudioFilePropertyAudioDataByteCount, &propertySize, &audioDataByteCount);
//    
//    if (0 != getSizeResult)
//    {
//        NSLog(@"An error occurred when attempting to determine the size of audio file %@: %ld", audioFilePath, getSizeResult);
//    }
//    
//    UInt32 bytesRead = (UInt32)audioDataByteCount;
//    
//    void *audioData = malloc(bytesRead);
//    
//    OSStatus readBytesResult = AudioFileReadBytes(afid, false, 0, &bytesRead, audioData);
//    
//    if (0 != readBytesResult)
//    {
//        NSLog(@"An error occurred when attempting to read data from audio file %@: %ld", audioFilePath, readBytesResult);
//    }
//    
//    AudioFileClose(afid);
//    
//    ALuint outputBuffer;
//    alGenBuffers(1, &outputBuffer);
//    
//    alBufferData(outputBuffer, AL_FORMAT_STEREO16, audioData, bytesRead, 44100);
//    
//    if (audioData)
//    {
//        free(audioData);
//        audioData = NULL;
//    }
//    
//    alSourcef(sourceID, AL_PITCH, 1.0f);
//    alSourcef(sourceID, AL_GAIN, 1.0f);
//    
//    alSourcei(sourceID, AL_BUFFER, outputBuffer);
//    
//    alSourcePlay(sourceID);
}

@end
