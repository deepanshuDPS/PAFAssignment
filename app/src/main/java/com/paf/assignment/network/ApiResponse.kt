package com.paf.assignment.network

import com.paf.assignment.responses.unsplash_images.UnsplashImagesResponse

class ApiResponse (val response: UnsplashImagesResponse?=null, val error:Throwable?=null)