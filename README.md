# Voices

Fast in-process text to speech for Java 17 and above. No external apis. No system dependencies.


* [sample 1](https://github.com/user-attachments/assets/3bb91fe5-682a-498b-ab38-3f4e0d1885f6)
* [sample 2](https://github.com/user-attachments/assets/3ff5dd48-df3f-4b47-9b4e-e88f97bf6d4d)

# What is this?

An easy-to-use local text to speech library for Java.

It can produce reasonable quality audio using low-specced hardware.

It provides several components

* Code to run the voice models from the [piper](https://github.com/rhasspy/piper) project
* A piper-compatible pure Java phonemizer for English partially ported from [phonemize](https://github.com/hans00/phonemize)
* Compatible phoneme dictionaries for uk and us English
* A multi-lingual phonemizer using the [onnx model](https://huggingface.co/OpenVoiceOS/g2p-mbyt5-12l-ipa-childes-espeak-onnx) from OpenVoiceOs 
* A small number of piper models available as dependencies on maven central
* Code to download other models not uploaded to central

The models are run using the onnxruntime library, so can utilise both CPU and GPU.

## Releases

See [Releases](https://github.com/hcoles/voices/releases)

## English-Only Usage With Rules Based Phonemizer

Using Voices requires three code dependencies and one or more models.

```xml
<!-- main dependency -->
<dependency>
    <groupId>org.pitest.voices</groupId>
    <artifactId>chorus</artifactId>
    <version>0.0.8</version>
</dependency>
<!-- a prepackaged model -->
<dependency>
    <groupId>org.pitest.voices</groupId>
    <artifactId>alba</artifactId>
    <version>0.0.8</version>
</dependency>
<!-- dictionary of pronunciations -->
<dependency>
    <groupId>org.pitest.voices</groupId>
    <artifactId>en_uk</artifactId> <!-- or en_us -->
    <version>0.0.8</version>
</dependency>
<!-- runtime for onnx models -->
<dependency>
    <groupId>com.microsoft.onnxruntime</groupId>
    <artifactId>onnxruntime</artifactId> <!-- or onnxruntime_gpu -->
    <version>1.22.0</version>
</dependency>
```

Technically, the rules based phonemizer can be used without a dictionary, but the quality of the speech would be poor.

The `Chorus` class acts as a manager for voice models, handling loading and freeing of resources. Loading is an expensive
operation, so it is recommended to keep a single instance of `Chorus` for the lifetime of your application.

```java
ChorusConfig config = chorusConfig(EnUkDictionary.en_uk());
try (Chorus chorus = new Chorus(config)) {
    Voice alba = chorus.voice(Alba.albaMedium());

    Audio audio = alba.say("Hello there, I'm vaguely Scottish.");
    audio.save(some path);
}
```

The example above uses a model retrieved at build time as a normal maven dependency.

A wider range of models can be retrieved at runtime by adding the model downloader dependency. 

```xml
<dependency>
    <groupId>org.pitest.voices</groupId>
    <artifactId>model-downloader</artifactId>
    <version>0.0.8</version>
</dependency>
```

Models can be retrieved using the factory methods on the

* org.pitest.voices.download.Models
* org.pitest.voices.download.UsModels
* org.pitest.voices.download.NonEnglishModels

Classes.

By default, voice models are downloaded to `~/.cache/voices/`, but this can be configured in ChorusConfig.

## Multi-lingual Usage

The OpenVoice phonemizer is much more capable than the rules-based one. It can be used without a dictionary to
create good quality speech in multiple languages (including English).

It is more heavy-weight, using a 50mb model (compared to 3mb for a dictionary file), and is more computationally 
expensive.

Once the dependency has been added

```xml
<dependency>
    <groupId>org.pitest.voices</groupId>
    <artifactId>openvoice-phonemizer</artifactId>
    <version>0.0.8</version>
</dependency>
```

The phonemizer can be selected with

```java
ChorusConfig config = chorusConfig(Dictionaries.empty())
        .withModel(new OpenVoiceSupplier());
```

## Running on GPU

Models can be run on GPU instead of CPU by using the `onnxruntime_gpu` dependency instead of `onnxruntime`. It is
important that only the `onnxruntime_gpu` dependency is on the classpath. If the standard `onnxruntime` is also present the model
will fail to load to gpu.

To activate the gpu, the gpuChorusConfig can be used.

```java
ChorusConfig config = gpuChorusConfig(EnUkDictionary.en_uk());
```

This runs the model on gpu 0 with no other options set. More complex setups can be configured using the `withCudaOptions`
method on ChorusConfig.

## Pauses

Voices will add pauses if it encounters the following markdown symbols

* Markdown `#` Style Headings
* Markdown `---` Section breaks
* Em dashes and Markdown --- em dashes
* En dashes and Markdown -- en dashes

The defaults can be adjusted via the ChorusConfig class.

## Hetronyms

Voice's hetronym (words with the same spelling but different pronunciations) handling can sometimes perform better
than piper and espeak-ng thanks to part of speech tagging provided by the OpenNLP library.

Phrases such as

* *I moped on my moped.*
* *I rebel because I am a rebel.*
* *Sow the seeds for the sow to eat.*

All use the correct pronunciations of the heterographs.

## Licencing

Most of the project is licenced under Apache 2. The en_uk dictionary is released under GPL 3 due to a cautious
interpretation of the licencing terms of the espeak-ng tool which was used to generate much of its content.

Although generally the GPL does not apply to the output of a program, it seems probable that feeding a word list
to espeak-ng will result in it regurgitating a significant proportion of its own internal dictionary.

The en_us dictionary is of lower quality, but is generated by transforming the CMU dictionary which, whilst copyrighted by
Carnegie Mellon University, is free to use so long as its copyright is acknowledged.

The models from the piper project are not part of this project and may have their own usage restrictions. Please 
check they match your use case.

## Alternatives

### Sherpa Onnx

The [Sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) project can also run piper models.

At the point this project was initiated, sherpa was difficult to consume as it was not available from maven central and required 
manual installation of native libraries. It also seemed to handle hetronyms poorly.

This situation may have since improved.

### Mary TTS

[Mary TTS](https://github.com/marytts/marytts) is very mature and produces reasonable quality speech, however it sounds a little
robotic by modern standards.

## Development

Although much of the ported logic is not well tested, there are a splattering of tests to prevent major regression
while changing things, and a few tests that by default play audio to allow experimentation. 

If you're building from the command line, the audio can be disabled with.

```bash
mvn -Dsilent=true install
```

## Background

I created this library to narrate my own writing as part of my editing loop. Initially
it called the sherpa native libraries, but I kept coming back to the idea of writing a pure
Java phonemizer as it would allow some degree of control over pauses, which are important
for narrating fiction.

I have no background in text to speech or linguistics, so much of the functionality relies on work
by other better qualified people.
