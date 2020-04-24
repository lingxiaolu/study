package com.zwq.service.impl;

import com.zwq.dao.ResumeDao;
import com.zwq.pojo.Resume;
import com.zwq.service.IResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IResumeServiceImpl implements IResumeService {

    @Autowired
    private ResumeDao resumeDao;
    @Override
    public List<Resume> queryResumeList() {
        return resumeDao.findAll();
    }

    @Override
    public void updateResume(Resume resume) {
        resumeDao.save(resume);
    }

    @Override
    public Optional toUpdateResume(Example example) {
        return resumeDao.findOne(example);
    }

    @Override
    public void removeResume(Resume resume) {
        resumeDao.delete(resume);
    }

    @Override
    public Resume insertResume(Resume resume) {
        return resumeDao.save(resume);
    }
}
