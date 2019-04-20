package com.catcher.base.data.dao

import javax.persistence.*

@Entity
@Table(name = "privileges")
data class Privilege(@Id @GeneratedValue(strategy = GenerationType.AUTO)
                     val id: Int,
                     val name: String)