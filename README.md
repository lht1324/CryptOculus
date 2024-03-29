# CryptOculus - 한국 암호화폐 거래소 시세 뷰어

## 개요

한국의 암호화폐 거래소인 코인원, 빗썸, 업비트의 시세 정보를 보여주는 안드로이드 앱입니다.

Kotlin 100%로 개발되었으며, MVVM 패턴을 적용해 개발하다 Repository 패턴으로 변경했고 RxKotlin을 사용해 비동기 처리를 구현했습니다.

| <img width="600" alt="mvvm pattern" src="https://user-images.githubusercontent.com/33789695/124359243-0ba07100-dc5f-11eb-847c-d0fe0a3846e5.png"> |
| :---: |
| MVVM 패턴 |
| <img width="600" alt="repository pattern" src="https://user-images.githubusercontent.com/33789695/124359274-32f73e00-dc5f-11eb-98c5-9bf058bcddb1.png"> |
| Repository 패턴 |

## 버전

* [1.0](https://github.com/lht1324/CryptOculus-ver.Java-Legacy/tree/master/app/src/main/java/org/techtown/cryptoculus)

프로토타입. 자바로 제작.

* [1.1](https://github.com/lht1324/CryptOculus-ver.Kotlin-Legacy/tree/master/app/src/main/java/org/techtown/cryptoculus)

자바에서 코틀린으로 변경, 디자인 패턴 미적용

* 1.2

Repository 패턴 적용. 데이터 업데이트 시 플레이스토어 업데이트가 필수였지만 파이어베이스를 이용해 실시간으로 변경 사항을 받아오도록 해 플레이스토어 의존을 최소화.

## 사용된 라이브러리

* AAC
  * Room
  * Data Binding
  * ViewModel
  * LiveData
* Rx
  * RxKotlin
  * RxBinding
* Firebase Firestore
* Glide
* Retrofit2

## 코드

[MainActivity.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/view/MainActivity.kt)

메인 액티비티를 다루는 코드입니다. MainViewModel, SortingViewModel, ImageViewModel, FirestoreViewModel 내부의 LiveData를 observe하며, 어댑터를 변경하거나 뷰 모델에 지시를 내립니다.

[MainViewModel.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/viewmodel/MainViewModel.kt)

메인 액티비티에서 사용되는 데이터의 로직을 맡는 뷰 모델입니다. 내부에서 전부 처리하기에 비효율적인 기능은 SortingViewModel, ImageViewModel, FirestoreViewModel로 분리했습니다. 사용되는 주 목적은 거래소에서 받아온 데이터를 가공하는 것입니다.

[SortingViewModel.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/viewmodel/SortingViewModel.kt)

RecyclerView의 아이템들을 정렬할 때 사용됩니다.

[ImageViewModel.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/viewmodel/ImageViewModel.kt)

RecyclerView 아이템의 이미지를 Storage에서 다운로드 받을 때 사용합니다.

[FirestoreViewModel.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/viewmodel/FirestoreViewModel.kt)

RecyclerView 아이템의 한글 화폐명을 Firestore에서 다운로드 받을 때 사용합니다.

[ChoiceActivity.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/view/ChoiceActivity.kt)

원하는 코인을 선택하는 액티비티입니다.

[ChoiceViewModel.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/viewmodel/ChoiceViewModel.kt)

원하는 코인을 선택하는 액티비티에서 사용되는 뷰 모델입니다.

[Repository.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/repository/Repository.kt), [RepositoryImpl.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/repository/RepositoryImpl.kt)

Repository Interface와 Repository Interface를 구현하는 클래스입니다.

[Client.kt](https://github.com/lht1324/CryptOculus/blob/master/app/src/main/java/org/techtown/cryptoculus/repository/network/Client.kt)

Retrofit, Glide 등 네트워크를 이용하는 라이브러리의 기능이 필요할 때 사용합니다.

## UI

 * **MainActivity**

| <img width="400" alt="main1" src="https://user-images.githubusercontent.com/33789695/124812177-3e6ba180-df9e-11eb-8ec6-34f584f1f0bb.jpg"> | <img width="400" alt="main2" src="https://user-images.githubusercontent.com/33789695/124812212-49becd00-df9e-11eb-8ad0-b331b0cb36dc.jpg"> | <img width="400" alt="main3" src="https://user-images.githubusercontent.com/33789695/124812251-53e0cb80-df9e-11eb-8061-44b13a2d4f6b.jpg"> |
| :---: | :---: | :---: |

 * **ChoiceActivity와 결과**

| <img width="400" alt="choice1" src="https://user-images.githubusercontent.com/33789695/124812253-5511f880-df9e-11eb-8019-09a2f4763431.jpg"> | <img width="400" alt="choice2" src="https://user-images.githubusercontent.com/33789695/124812256-55aa8f00-df9e-11eb-9c3e-51e33aa9c039.jpg"> | <img width="400" alt="choice3" src="https://user-images.githubusercontent.com/33789695/124812260-55aa8f00-df9e-11eb-9b93-700c4fca7a80.jpg"> |
| :---: | :---: | :---: |

## License

MIT License

Copyright (c) 2021 OverEasy (Jaeho Lee)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
