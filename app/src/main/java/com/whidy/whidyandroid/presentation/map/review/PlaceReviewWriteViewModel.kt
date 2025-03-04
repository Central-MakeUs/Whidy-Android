package com.whidy.whidyandroid.presentation.map.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whidy.whidyandroid.data.review.ReviewRequest
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaceReviewWriteViewModel : ViewModel() {

    private val _score = MutableLiveData<Double>()
    val score: LiveData<Double> get() = _score

    private val _keywordList = MutableLiveData<List<String>>()
    val keywordList: LiveData<List<String>> get() = _keywordList

    private val _howToUse = MutableLiveData<String>()
    val howToUse: LiveData<String> get() = _howToUse

    private val _howLongWait = MutableLiveData<String>()
    val howLongWait: LiveData<String> get() = _howLongWait

    private val _withWho = MutableLiveData<String>()
    val withWho: LiveData<String> get() = _withWho

    private val _visitPurpose = MutableLiveData<List<String>>()
    val visitPurpose: LiveData<List<String>> get() = _visitPurpose

    fun setScore(rating: Double) {
        _score.value = rating
    }

    fun setKeyword(selectedList: List<String>) {
        _keywordList.value = selectedList
    }

    fun setHowToUse(value: String) {
        _howToUse.value = value
    }

    fun setHowLongWait(value: String) {
        _howLongWait.value = value
    }

    fun setWithWho(value: String) {
        _withWho.value = value
    }

    fun setVisitPurpose(selectedList: List<String>) {
        _visitPurpose.value = selectedList
    }

    fun postReview(placeId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val reviewRequest = ReviewRequest(
                    reviewId = 0,
                    placeId = placeId,
                    score = _score.value ?: 0.0,
                    keywords = _keywordList.value ?: emptyList(),
                    isReserved = when (_howToUse.value) {
                        "예약 사용" -> true
                        else -> false
                    },
                    waitTime = _howLongWait.value ?: "NO_WAIT",
                    visitPurposes = _visitPurpose.value ?: emptyList(),
                    companionType = _withWho.value ?: "SOLO"
                )
                RetrofitClient.reviewService.postReview(reviewRequest)
                Timber.d("Review posted successfully: $reviewRequest")
                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "Error posting review for placeId $placeId")
                onFailure()
            }
        }
    }

    // 기존 리뷰가 있는 경우 해당 리뷰 id를 저장 (null이면 신규)
    private val _existingReviewId = MutableLiveData<Int?>()
    val existingReviewId: LiveData<Int?> get() = _existingReviewId

    fun setExistingReviewId(id: Int?) { _existingReviewId.value = id }

    fun updateReview(placeId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                // 여기서는 reviewRequest.reviewId 값은 필요에 따라 서버에서 무시하거나 기존 review id를 넣어줄 수 있음.
                val reviewRequest = ReviewRequest(
                    reviewId = _existingReviewId.value ?: 0,
                    placeId = placeId,
                    score = _score.value ?: 0.0,
                    keywords = _keywordList.value ?: emptyList(),
                    isReserved = when (_howToUse.value) {
                        "예약 사용" -> true
                        else -> false
                    },
                    waitTime = _howLongWait.value ?: "NO_WAIT",
                    visitPurposes = _visitPurpose.value ?: emptyList(),
                    companionType = _withWho.value ?: "SOLO"
                )
                RetrofitClient.reviewService.updateReview(reviewRequest)
                Timber.d("Review updated: $reviewRequest")
                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "Error updating review for placeId $placeId")
                onFailure()
            }
        }
    }
}