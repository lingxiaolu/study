package com.zwq.service;

import com.zwq.pojo.Resume;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;

public interface IResumeService {
    List<Resume> queryResumeList();

    void updateResume(Resume resume);

    Optional toUpdateResume(Example example);

    void removeResume(Resume resume);

    Resume insertResume(Resume resume);
}
