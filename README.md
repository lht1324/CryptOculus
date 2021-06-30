# CryptOculus - 한국 암호화폐 거래소 시세 뷰어

## 개요

한국의 암호화폐 거래소인 코인원, 빗썸, 업비트의 시세 정보를 보여주는 안드로이드 앱입니다.

Kotlin 100%로 개발되었으며, MVVM 패턴을 적용해 개발하다 Repository 패턴으로 변경했고 RxKotlin을 사용해 비동기 처리를 구현했습니다.

## 버전

* [1.0](https://github.com/lht1324/CryptOculus-ver.Java-Legacy)

프로토타입. 자바로 제작.

* [1.1](https://github.com/lht1324/CryptOculus-ver.Kotlin-Legacy)

자바에서 코틀린으로 변경, 디자인 패턴 미적용

* 1.2

리포지토리 패턴 적용. 데이터 업데이트 시 플레이스토어 업데이트가 필수였지만 파이어베이스를 이용해 실시간으로 변경 사항을 받아오도록 해 플레이스토어 의존을 최소화.

## 사용된 라이브러리

* AAC
  * Room
  * Data Binding
  * ViewModel
  * LiveData
* Rx
  * RxKotlin
  * RxBinding
* RecyclerView
* Firebase Firestore
* SharedPreferences
* Glide
* Retrofit2
  * RxJava2CallAdapterFactory
  * GsonConverterFactory
* Gson
* Firebase Firestore

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
